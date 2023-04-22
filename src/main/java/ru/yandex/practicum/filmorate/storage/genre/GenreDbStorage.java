package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM GENRE";
        return jdbcTemplate.query(sql, this::mapRowToGenre);
    }

    @Override
    public Genre get(int id) {
        String sql = "SELECT * FROM GENRE WHERE GENRE_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToGenre, id);
        } catch (Exception e) {
            throw new NotFoundException("No such genre in the database");
        }

    }
    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("GENRE_ID"))
                .name(resultSet.getString("GENRE_NAME"))
                .build();
    }
}
