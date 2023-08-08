package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;

    public Review createReview(Review review) throws ValidationException {
        checkId(review.getFilmId());
        checkId(review.getUserId());
        return reviewStorage.createReview(review);
    }

    public Review updateReview(Review review) throws ValidationException, ExistenceException {
        checkId(review.getReviewId());
        checkId(review.getFilmId());
        checkId(review.getUserId());
        return reviewStorage.updateReview(review);
    }

    public void removeReviewById(int id) throws ValidationException {
        checkId(id);
        reviewStorage.removeReviewById(id);
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
    }

    public void putDislike(int id, int userId) throws ValidationException {
        checkId(id);
        checkId(userId);
        reviewStorage.putDislike(id, userId);
    }

    public void removeLike(int id, int userId) throws ValidationException {
        checkId(id);
        checkId(userId);
        reviewStorage.removeLike(id, userId);
    }

    public void removeDislike(int id, int userId) throws ValidationException {
        checkId(id);
        checkId(userId);
        reviewStorage.removeDislike(id, userId);
    }
}
