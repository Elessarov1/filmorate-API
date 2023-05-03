package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.enums.FilmStorageSql.*;
import static ru.yandex.practicum.filmorate.model.enums.EventType.LIKE;
import static ru.yandex.practicum.filmorate.model.enums.Operation.ADD;
import static ru.yandex.practicum.filmorate.model.enums.Operation.REMOVE;

@Slf4j
@RequiredArgsConstructor
@Repository
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    private final EventStorage eventStorage;

    @Override
    public Film add(Film film) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        validateFilm(film);
        Number key = jdbcInsert.withTableName("FILM")
                .usingGeneratedKeyColumns("ID")
                .executeAndReturnKey(getFilmFields(film));
        film.setId(key.intValue());

        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        List<Map<String, Object>> batchValues = new ArrayList<>();
        for (Genre genre : film.getGenres()) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("FILM_ID", film.getId());
            parameters.put("GENRE_ID", genre.getId());
            batchValues.add(parameters);
        }
        jdbcInsert.withTableName("FILM_GENRE")
                .executeBatch(batchValues.toArray(new Map[batchValues.size()]));
        batchValues.clear();

        addDirectors(film);

        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        for (Integer like : film.getLikes()) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("FILM_ID", film.getId());
            parameters.put("USER_ID", like);
            batchValues.add(parameters);
        }
        jdbcInsert.withTableName("LIKES")
                .executeBatch(batchValues.toArray(new Map[batchValues.size()]));
        return get(key.intValue());
    }

    @Override
    public Film update(Film film) {
        int filmId = film.getId();
        String sqlQuery = "UPDATE FILM SET NAME = ?, DESCRIPTION = ?, DURATION = ?, " +
                "RELEASE_DATE = ?, MPA_ID = ? WHERE ID = ?";
        int rowsUpdated = jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getMpa().getId(),
                filmId
        );
        if (rowsUpdated == 0) {
            throw new NotFoundException("No such film in the database");
        }
        removeOldGenres(filmId);
        List<Integer> genreIds = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toList());

        if (!genreIds.isEmpty()) {
            SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
            List<Map<String, Object>> batchValues = new ArrayList<>();
            for (Genre genre : film.getGenres()) {
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("FILM_ID", film.getId());
                parameters.put("GENRE_ID", genre.getId());
                batchValues.add(parameters);
            }
            jdbcInsert.withTableName("FILM_GENRE")
                    .executeBatch(batchValues.toArray(new Map[batchValues.size()]));
        }

        removeDirectors(filmId);
        List<Integer> directorIds = film.getDirectors().stream()
                .map(Director::getId)
                .collect(Collectors.toList());

        if (!directorIds.isEmpty()) {
            addDirectors(film);
        }

        return get(filmId);
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT f.ID, f.NAME, f.DESCRIPTION, f.DURATION, f.RELEASE_DATE," +
                "f.MPA_ID, m.NAME AS MPA_NAME " +
                "FROM FILM AS f " +
                "JOIN MPA AS m ON f.MPA_ID = m.MPA_ID";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public boolean delete(int id) {
        String sqlQuery = "DELETE FROM FILM WHERE ID = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    @Override
    public Film get(int id) {
        String sqlQuery = "SELECT f.ID, f.NAME, f.DESCRIPTION, f.DURATION, f.RELEASE_DATE, " +
                "f.MPA_ID, m.NAME AS MPA_NAME " +
                "FROM FILM AS f " +
                "JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                "WHERE ID = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("No such film in the database");
        }
    }

    @Override
    public boolean addLikeToFilm(int filmId, int userId) {
        String sql = "INSERT INTO LIKES (FILM_ID, USER_ID) VALUES(?,?)";
        if (jdbcTemplate.update(sql, filmId, userId) > 0) {
            Event event = Event.builder()
                    .timestamp(System.currentTimeMillis())
                    .userId(userId)
                    .eventType(LIKE)
                    .operation(ADD)
                    .entityId(filmId)
                    .build();
            eventStorage.add(event);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteLike(int filmId, int userId) {
        if (filmId < 0 || userId < 0) {
            throw new NotFoundException("Invalid Data");
        }
        String sql = "DELETE FROM LIKES WHERE FILM_ID AND USER_ID IN(?,?)";
        if (jdbcTemplate.update(sql, filmId, userId) > 0) {
            Event event = Event.builder()
                    .timestamp(System.currentTimeMillis())
                    .userId(userId)
                    .eventType(LIKE)
                    .operation(REMOVE)
                    .entityId(filmId)
                    .build();
            eventStorage.add(event);
            return true;
        }
        return false;
    }

    @Override
    public List<Film> getFilmsByDirector(int directorId) {
        String sql = "SELECT f.ID, f.NAME, f.DESCRIPTION, f.DURATION, f.RELEASE_DATE, " +
                "f.MPA_ID, m.NAME AS MPA_NAME " +
                "FROM FILM AS f " +
                "JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                "JOIN FILM_DIRECTOR AS fd ON f.ID = fd.FILM_ID " +
                "WHERE fd.DIRECTOR_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, directorId);
    }

    @Override
    public List<Film> findByRequestedDirector(String query) {
        return jdbcTemplate.query(FIND_BY_REQUESTED_DIRECTOR.getSql(), this::mapRowToFilm, query);
    }

    @Override
    public List<Film> findByRequestedTitle(String query) {
        return jdbcTemplate.query(FIND_BY_REQUESTED_TITLE.getSql(), this::mapRowToFilm, query);
    }

    @Override
    public List<Film> findByRequestedTitleAndDirector(String query) {
        return jdbcTemplate.query(FIND_BY_REQUESTED_TITLE_AND_DIRECTOR.getSql(), this::mapRowToFilm, query, query);
    }

    @Override
    public List<Film> getByGenreAndYear(int count, int genreId, int year) {
        return jdbcTemplate.query(GET_BY_GENRE_AND_YEAR.getSql(), this::mapRowToFilm, genreId, year, count);
    }

    @Override
    public List<Film> getByGenreOrYear(int count, int genreId, int year) {
        return jdbcTemplate.query(GET_BY_GENRE_OR_YEAR.getSql(), this::mapRowToFilm, genreId, year, count);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getInt("ID"))
                .name(resultSet.getString("NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .duration(resultSet.getInt("DURATION"))
                .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
                .mpa(new Mpa(resultSet.getInt("MPA_ID"), resultSet.getString("MPA_NAME")))
                .build();
        film.getGenres().addAll(getGenresByFilmId(film.getId()));
        film.getLikes().addAll(getLikesByFilmId(film.getId()));
        film.getDirectors().addAll(getDirectorsByFilmId(film.getId()));
        return film;
    }

    private Map<String, Object> getFilmFields(Film film) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("NAME", film.getName());
        fields.put("DESCRIPTION", film.getDescription());
        fields.put("DURATION", film.getDuration());
        fields.put("RELEASE_DATE", film.getReleaseDate());
        if (film.getMpa() != null) {
            fields.put("MPA_ID", film.getMpa().getId());
        }
        return fields;
    }

    private List<Genre> getGenresByFilmId(int id) {
        String sql = "SELECT * FROM GENRE WHERE GENRE_ID IN (SELECT GENRE_ID FROM FILM_GENRE WHERE FILM_ID = ?)";
        return jdbcTemplate.query(sql, this::mapRowToGenre, id);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("GENRE_ID"))
                .name(resultSet.getString("GENRE_NAME"))
                .build();
    }

    private List<Director> getDirectorsByFilmId(int id) {
        String sql = "SELECT * FROM DIRECTOR " +
                "WHERE DIRECTOR_ID IN (SELECT DIRECTOR_ID FROM FILM_DIRECTOR WHERE FILM_ID = ?)";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new Director(
                        rs.getInt("DIRECTOR_ID"),
                        rs.getString("DIRECTOR_NAME")),
                id);
    }

    private Set<Integer> getLikesByFilmId(int id) {
        String sql = "SELECT USER_ID FROM LIKES WHERE FILM_ID = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        Set<Integer> res = new HashSet<>();
        while (rowSet.next()) {
            res.add(rowSet.getInt("USER_ID"));
        }
        return res;
    }

    private void validateFilm(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("invalid release date");
        }
    }

    private boolean removeOldGenres(int filmId) {
        String sql = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        return jdbcTemplate.update(sql, filmId) > 0;
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        String sqlQuery = "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE , f.DURATION, f.MPA_ID, " +
                "r.NAME AS MPA_NAME, p.popularity FROM FILM AS f " +
                "JOIN LIKES l ON f.ID  = l.FILM_ID " +
                "JOIN LIKES ls ON f.ID = ls.FILM_ID " +
                "JOIN (SELECT FILM_ID, COUNT (FILM_ID) AS popularity " +
                "FROM LIKES GROUP BY FILM_ID) AS p ON p.FILM_ID  = f.ID " +
                "JOIN MPA AS r ON f.MPA_ID  = r.MPA_ID " +
                "WHERE l.USER_ID  = ? AND ls.USER_ID  = ? " +
                "GROUP BY f.ID ORDER BY p.popularity DESC;";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, userId, friendId);
    }

    private boolean removeDirectors(int filmId) {
        String sql = "DELETE FROM FILM_DIRECTOR WHERE FILM_ID = ?";
        return jdbcTemplate.update(sql, filmId) > 0;
    }

    private void addDirectors(Film film) {
        List<Map<String, Object>> batchValues = new ArrayList<>();
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);

        for (Director director : film.getDirectors()) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("DIRECTOR_ID", director.getId());
            parameters.put("FILM_ID", film.getId());
            batchValues.add(parameters);
        }
        jdbcInsert.withTableName("FILM_DIRECTOR")
                .executeBatch(batchValues.toArray(new Map[batchValues.size()]));
    }
}
