package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventOperation;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.validator.ReviewValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final EventService eventService;

    public Review createReview(Review review) {
        ReviewValidator.validateReview(review);
        checkId(review.getFilmId());
        checkId(review.getUserId());
        Review toReturn = reviewStorage.createReview(review);
        eventService.addEvent(new Event(
                0,
                System.currentTimeMillis(),
                toReturn.getUserId(),
                EventType.REVIEW,
                EventOperation.ADD,
                toReturn.getReviewId()));
        return toReturn;
    }

    public Review updateReview(Review review) {
        ReviewValidator.validateReview(review);
        checkId(review.getReviewId());
        checkId(review.getFilmId());
        checkId(review.getUserId());
        Review toReturn = reviewStorage.updateReview(review);
        eventService.addEvent(new Event(
                0,
                System.currentTimeMillis(),
                toReturn.getUserId(),
                EventType.REVIEW,
                EventOperation.UPDATE,
                toReturn.getReviewId()));
        return toReturn;
    }

    public void removeReviewById(int id) {
        checkId(id);
        Review review = reviewStorage.removeReviewById(id);
        eventService.addEvent(new Event(
                id,
                System.currentTimeMillis(),
                review.getUserId(),
                EventType.REVIEW,
                EventOperation.REMOVE,
                review.getFilmId()));
    }

    private void checkId(Integer id) {
        if (id == null) {
            throw new ValidationException("id не передан");
        } else if (id <= 0) {
            throw new NotFoundException("id должен быть положительным");
        }
    }

    public Review getReviewById(int id) {
        checkId(id);
        return reviewStorage.getReviewById(id);
    }

    public List<Review> getAllReviews(Integer filmId, int count) {
        List<Review> reviews;
        if (filmId == null) {
            reviews = reviewStorage.getAllReviews(count);
        } else {
            reviews = reviewStorage.getAllReviewsByFilmId(filmId, count);
        }
        return reviews;
    }

    public void putLike(int id, int userId) {
        checkId(id);
        checkId(userId);
        reviewStorage.putLike(id, userId);
    }

    public void putDislike(int id, int userId) {
        checkId(id);
        checkId(userId);
        reviewStorage.putDislike(id, userId);
    }

    public void removeLike(int id, int userId) {
        checkId(id);
        checkId(userId);
        reviewStorage.removeLike(id, userId);
    }

    public void removeDislike(int id, int userId) {
        checkId(id);
        checkId(userId);
        reviewStorage.removeDislike(id, userId);
    }
}
