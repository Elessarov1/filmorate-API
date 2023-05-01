package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review add(Review review);

    Review update(Review review);

    List<Review> getAllReviews();

    List<Review> getAllReviewsByFilm(int filmId);

    boolean delete(int id);

    Review get(int id);

    boolean addLikeToReview(int id, int userId);

    boolean addDislikeToReview(int id, int userId);

    boolean deleteLike(int id, int userId);

    boolean deleteDislike(int id, int userId);
}
