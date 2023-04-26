package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

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
                Map.of("DIRECTOR_ID", director.getName())).intValue();
        director.setId(dirId);

        return director;
    }

    @Override
    public Director update(Director director) {
        return null;
    }

    @Override
    public void delete(int directorId) {

    }

    @Override
    public List<Director> getAllDirectors() {
        return null;
    }

    @Override
    public Director getById(int directorId) {
        return null;
    }
}
