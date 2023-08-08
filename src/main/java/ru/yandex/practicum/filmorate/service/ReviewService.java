package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventOperation;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final EventService eventService;

    public Review createReview(Review review) throws ValidationException {
        checkId(review.getFilmId());
        checkId(review.getUserId());
        Review toReturn = reviewStorage.createReview(review);
        eventService.addEvent(new Event(
                0,
                System.currentTimeMillis(),
                review.getUserId(),
                EventType.REVIEW,
                EventOperation.ADD,
                review.getFilmId()));
        return toReturn;
    }

    public Review updateReview(Review review) throws ValidationException, ExistenceException {
        checkId(review.getReviewId());
        checkId(review.getFilmId());
        checkId(review.getUserId());
        Review toReturn = reviewStorage.updateReview(review);
        eventService.addEvent(new Event(
                0,
                System.currentTimeMillis(),
                review.getUserId(),
                EventType.REVIEW,
                EventOperation.UPDATE,
                review.getFilmId()));
        return toReturn;
    }

    public void removeReviewById(int id) throws ValidationException, ExistenceException {
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

    private void checkId(Integer id) throws NotFoundException, ValidationException {
        if (id == null) {
            throw new ValidationException("id не передан");
        } else if (id <= 0) {
            throw new NotFoundException("id должен быть положительным");
        }
    }

    public Review getReviewById(int id) throws ValidationException, ExistenceException {
        checkId(id);
        Review review = reviewStorage.getReviewById(id);
        return review;
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

    public void putLike(int id, int userId) throws ValidationException {
        checkId(id);
        checkId(userId);
        reviewStorage.putLike(id, userId);
        eventService.addEvent(new Event(
                0,
                System.currentTimeMillis(),
                userId,
                EventType.LIKE,
                EventOperation.ADD,
                id));
    }

    public void putDislike(int id, int userId) throws ValidationException {
        checkId(id);
        checkId(userId);
        reviewStorage.putDislike(id, userId);
        /*eventService.addEvent(new Event(
                0,
                System.currentTimeMillis(),
                userId,
                EventType.DISLIKE,
                EventOperation.ADD,
                id));*/
    }

    public void removeLike(int id, int userId) throws ValidationException {
        checkId(id);
        checkId(userId);
        reviewStorage.removeLike(id, userId);
        eventService.addEvent(new Event(
                0,
                System.currentTimeMillis(),
                userId,
                EventType.LIKE,
                EventOperation.REMOVE,
                id));
    }

    public void removeDislike(int id, int userId) throws ValidationException {
        checkId(id);
        checkId(userId);
        reviewStorage.removeDislike(id, userId);
        /*eventService.addEvent(new Event(
                0,
                System.currentTimeMillis(),
                userId,
                EventType.DISLIKE,
                EventOperation.REMOVE,
                id));*/
    }
}
