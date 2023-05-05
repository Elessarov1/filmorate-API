package ru.yandex.practicum.filmorate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
@Tag(name = "Reviews", description = "Methods for working with reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Add review in the database")
    public Review addReview(@Valid @RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping
    @Operation(summary = "Update review in the database")
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove review from database")
    public boolean deleteReview(@PathVariable int id) {
        return reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get review by ID")
    public Review getReview(@PathVariable int id) {
        return reviewService.getReview(id);
    }

    @GetMapping
    @Operation(summary = "Get all reviews or by film ID")
    public List<Review> getReviews(@RequestParam(required = false) Integer filmId,
                                   @RequestParam(required = false, defaultValue = "10") int count) {
        return reviewService.getReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    @Operation(summary = "Add like to review")
    public boolean addLike(@PathVariable int id, @PathVariable int userId) {
        return reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    @Operation(summary = "Add dislike to review")
    public boolean addDislike(@PathVariable int id, @PathVariable int userId) {
        return reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @Operation(summary = "Remove like from review")
    public boolean deleteLike(@PathVariable int id, @PathVariable int userId) {
        return reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    @Operation(summary = "Remove dislike from review")
    public boolean deleteDislike(@PathVariable int id, @PathVariable int userId) {
        return reviewService.deleteDislike(id, userId);
    }
}
