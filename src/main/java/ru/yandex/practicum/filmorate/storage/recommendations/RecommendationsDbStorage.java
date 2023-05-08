package ru.yandex.practicum.filmorate.storage.recommendations;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RecommendationsDbStorage implements RecommendationStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Integer> getUserLikes(int id) {
        String sql = "SELECT FILM_ID FROM LIKES WHERE USER_ID = ?";
        return jdbcTemplate.queryForList(sql, Integer.class, id);
    }
}
