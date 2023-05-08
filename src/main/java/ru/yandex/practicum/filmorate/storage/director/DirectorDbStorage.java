package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director create(Director director) {
        SimpleJdbcInsert simpleInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("DIRECTOR")
                .usingGeneratedKeyColumns("DIRECTOR_ID");

        int dirId = simpleInsert.executeAndReturnKey(
                Map.of("DIRECTOR_NAME", director.getName())).intValue();
        director.setId(dirId);

        return director;
    }

    @Override
    public Director update(Director director) {
        String sql = "UPDATE DIRECTOR SET DIRECTOR_NAME = ? WHERE DIRECTOR_ID = ?";
        int rowUpdated = jdbcTemplate.update(sql, director.getName(), director.getId());
        if (rowUpdated == 0) {
            throw new NotFoundException("Director not found.");
        }
        return director;
    }

    @Override
    public void delete(int directorId) {
        String sqlDeleteConnection = "DELETE FROM FILM_DIRECTOR WHERE DIRECTOR_ID = ?";
        String sqlDeleteDirector = "DELETE FROM DIRECTOR WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sqlDeleteConnection, directorId);
        jdbcTemplate.update(sqlDeleteDirector, directorId);
    }

    @Override
    public List<Director> getAllDirectors() {
        String sql = "SELECT * FROM DIRECTOR";
        return jdbcTemplate.query(sql, this::makeDirector);
    }

    @Override
    public Director getById(int directorId) {
        String sql = "SELECT * FROM DIRECTOR WHERE DIRECTOR_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::makeDirector, directorId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Director is not found.");
        }
    }

    private Director makeDirector(ResultSet rs, int rowNum) throws SQLException {
        return new Director(rs.getInt("DIRECTOR_ID"), rs.getString("DIRECTOR_NAME"));
    }
}
