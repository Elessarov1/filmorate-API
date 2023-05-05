package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;

    public Review addReview(Review review) {
        userStorage.get(review.getUserId());
        filmStorage.get(review.getFilmId());
        return reviewStorage.add(review);
    }

    public Review updateReview(Review review) {
        return reviewStorage.update(review);
    }

    public boolean deleteReview(int id) {
        return reviewStorage.delete(id);
    }

    public Review getReview(int id) {
        return reviewStorage.get(id);
    }

    public List<Review> getReviews(Integer filmId, int count) {
        if (filmId == null) {
            return reviewStorage.getAllReviews().stream()
                    .sorted(Comparator.comparingInt(Review::getUseful).reversed())
                    .limit(count).collect(Collectors.toList());
        }
        return reviewStorage.getAllReviewsByFilm(filmId).stream()
                .sorted(Comparator.comparingInt(Review::getUseful).reversed())
                .limit(count).collect(Collectors.toList());
    }

    public boolean addLike(int id, int userId) {
        userStorage.get(userId);
        return reviewStorage.addLikeToReview(id, userId);
    }

    public boolean addDislike(int id, int userId) {
        return reviewStorage.addDislikeToReview(id, userId);
    }

    public boolean deleteLike(int id, int userId) {
        return reviewStorage.deleteLike(id, userId);
    }

    public boolean deleteDislike(int id, int userId) {
        return reviewStorage.deleteDislike(id, userId);
    }
}
