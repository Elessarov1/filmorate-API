package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.yandex.practicum.filmorate.model.enums.EventType.REVIEW;
import static ru.yandex.practicum.filmorate.model.enums.Operation.*;

@Repository
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    private final EventStorage eventStorage;

    private int calculateUseful(int id) {
        String sql = "SELECT SUM(RATING) FROM REVIEW_LIKES WHERE REVIEW_ID = ?";
        Integer reviewRating = jdbcTemplate.queryForObject(sql, Integer.class, id);
        if (reviewRating == null) {
            return 0;
        }
        return reviewRating;
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getInt("ID"))
                .content(resultSet.getString("DESCRIPTION"))
                .isPositive(resultSet.getBoolean("IS_POSITIVE"))
                .userId(resultSet.getInt("USER_ID"))
                .filmId(resultSet.getInt("FILM_ID"))
                .useful(calculateUseful(resultSet.getInt("ID")))
                .build();
    }

    private Map<String, Object> getReviewFields(Review review) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("DESCRIPTION", review.getContent());
        fields.put("IS_POSITIVE", review.getIsPositive());
        fields.put("USER_ID", review.getUserId());
        fields.put("FILM_ID", review.getFilmId());
        return fields;
    }

    @Override
    public Review add(Review review) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        Map<String, Object> reviewFields = getReviewFields(review);
        Number generatedReviewId = jdbcInsert.withTableName("REVIEWS")
                .usingGeneratedKeyColumns("ID")
                .executeAndReturnKey(reviewFields);
        review.setReviewId(generatedReviewId.intValue());

        Event event = Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId((Integer) reviewFields.get("USER_ID"))
                .eventType(REVIEW)
                .operation(ADD)
                .entityId(review.getReviewId())
                .build();
        eventStorage.add(event);
        return review;
    }

    @Override
    public Review update(Review review) {
        String sql = "UPDATE REVIEWS SET DESCRIPTION = ?, IS_POSITIVE = ?" +
                "WHERE ID = ?";
        int rowsUpdated = jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId()
        );
        if (rowsUpdated == 0) {
            throw new NotFoundException("No such review in the database");
        }
        Review updatedReview = get(review.getReviewId());
        Event event = Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(updatedReview.getUserId())
                .eventType(REVIEW)
                .operation(UPDATE)
                .entityId(updatedReview.getReviewId())
                .build();
        eventStorage.add(event);
        return updatedReview;
    }

    @Override
    public List<Review> getAllReviews() {
        String sql = "SELECT * FROM REVIEWS";
        return jdbcTemplate.query(sql, this::mapRowToReview);
    }

    @Override
    public List<Review> getAllReviewsByFilm(int filmId) {
        String sql = "SELECT * FROM REVIEWS WHERE FILM_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToReview, filmId);
    }

    @Override
    public boolean delete(int id) {
        Review review = get(id);
        Event event = Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(review.getUserId())
                .eventType(REVIEW)
                .operation(REMOVE)
                .entityId(id)
                .build();
        eventStorage.add(event);
        String sql = "DELETE FROM REVIEWS WHERE ID = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    @Override
    public Review get(int id) {
        String sql = "SELECT * FROM REVIEWS WHERE ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToReview, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("No such review in the database");
        }
    }

    @Override
    public boolean addLikeToReview(int id, int userId) {
        String sql = "MERGE INTO REVIEW_LIKES AS R " +
                "USING (SELECT " + id + " AS REVIEW_ID, " + userId + " AS USER_ID) AS S ON R.REVIEW_ID = S.REVIEW_ID AND R.USER_ID = S.USER_ID " +
                "WHEN NOT MATCHED THEN " +
                "   INSERT VALUES(?, ?, 1) " +
                "WHEN MATCHED THEN " +
                "   UPDATE SET R.RATING = 1 ";
        return jdbcTemplate.update(sql, id, userId) > 0;
    }

    @Override
    public boolean addDislikeToReview(int id, int userId) {
        String sql = "MERGE INTO REVIEW_LIKES AS R " +
                "USING (SELECT " + id + " AS REVIEW_ID, " + userId + " AS USER_ID) AS S ON R.REVIEW_ID = S.REVIEW_ID AND R.USER_ID = S.USER_ID " +
                "WHEN NOT MATCHED THEN " +
                "   INSERT VALUES(?, ?, -1) " +
                "WHEN MATCHED THEN " +
                "   UPDATE SET R.RATING = -1 ";
        return jdbcTemplate.update(sql, id, userId) > 0;
    }

    @Override
    public boolean deleteLike(int id, int userId) {
        String sql = "DELETE FROM REVIEW_LIKES WHERE ID = ? AND USER_ID = ? AND RATING = 1";
        return jdbcTemplate.update(sql, id, userId) > 0;
    }

    @Override
    public boolean deleteDislike(int id, int userId) {
        String sql = "DELETE FROM REVIEW_LIKES WHERE ID = ? AND USER_ID = ? AND RATING = -1";
        return jdbcTemplate.update(sql, id, userId) > 0;
    }
}
