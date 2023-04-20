package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film add(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        Number key = simpleJdbcInsert.withTableName("FILM")
                .usingGeneratedKeyColumns("ID")
                .executeAndReturnKey(getFilmFields(film));
        if (!film.getGenres().isEmpty()) {
            simpleJdbcInsert.withTableName("FILM_GENRE")
                    .execute(getFilmGenres(film));
        }
        if (!film.getLikes().isEmpty()) {
            simpleJdbcInsert.withTableName("LIKES")
                    .execute(getFilmLikes(film));
        }
        film.setId(key.intValue());
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE FILM SET NAME = ?, DESCRIPTION = ?, DURATION = ?, RELEASE_DATE = ?, MPA_ID = ? " +
                "WHERE ID = ?";
        int rowsUpdated = jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getMpa().getId(),
                film.getId()
        );
        if (rowsUpdated == 0) {
            throw new NotFoundException("No such film in the database");
        }
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT f.ID,f.NAME,f.DESCRIPTION,f.DURATION, f.RELEASE_DATE,f.MPA_ID,m.NAME AS MPA_NAME " +
                "FROM FILM AS f " +
                "JOIN MPA AS m ON f.MPA_ID = m.MPA_ID ";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public boolean delete(int id) {
        String sqlQuery = "DELETE FROM FILM WHERE ID = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    @Override
    public Film get(int id) {
        String sqlQuery = "SELECT f.ID,f.NAME,f.DESCRIPTION,f.DURATION, f.RELEASE_DATE,f.MPA_ID,m.NAME AS MPA_NAME " +
                "FROM FILM AS f " +
                "JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                "WHERE ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
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
    private Map<String, Object> getFilmGenres(Film film) {
        Map<String, Object> genres = new HashMap<>();
        film.getGenres().forEach(
                genre -> genres.put(String.valueOf(film.getId()), genre)
        );
        return genres;
    }

    private Map<String, Object> getFilmLikes(Film film) {
        Map<String, Object> likes = new HashMap<>();
        film.getLikes().forEach(
                like -> likes.put(String.valueOf(film.getId()), like)
        );
        return likes;
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

    private Set<Integer> getLikesByFilmId(int id) {
        String sql = "SELECT USER_ID FROM LIKES WHERE FILM_ID = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        Set<Integer> res = new HashSet<>();
        while (rowSet.next()) {
            res.add(rowSet.getInt("USER_ID"));
        }
        return res;
    }
}