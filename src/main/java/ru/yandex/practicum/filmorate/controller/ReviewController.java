package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Component
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody Review review) throws ValidationException {
        return new ResponseEntity<>(reviewService.createReview(review), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<?> updateReview(@RequestBody Review review) throws ValidationException, ExistenceException {
        return new ResponseEntity<>(reviewService.updateReview(review), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeReviewById(@PathVariable int id) throws ValidationException, ExistenceException {
        reviewService.removeReviewById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable int id) throws ExistenceException, ValidationException {
        return new ResponseEntity<>(reviewService.getReviewById(id), HttpStatus.OK);
    }

    @GetMapping
    public List<Review> getAllReviews(
            @RequestParam(value = "filmId", required = false) Integer filmId,
            @RequestParam(value = "count", defaultValue = "10") int count) {
        return reviewService.getAllReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<?> putLike(@PathVariable int id,
                                     @PathVariable int userId) throws ValidationException {
        reviewService.putLike(id, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ResponseEntity<?> putDislike(@PathVariable int id,
                                        @PathVariable int userId) throws ValidationException {
        reviewService.putDislike(id, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<?> removeLike(@PathVariable int id,
                                        @PathVariable int userId) throws ValidationException {
        reviewService.removeLike(id, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ResponseEntity<?> removeDislike(@PathVariable int id,
                                           @PathVariable int userId) throws ValidationException {
        reviewService.removeDislike(id, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
