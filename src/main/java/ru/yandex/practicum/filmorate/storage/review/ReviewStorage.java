package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.List;

public interface ReviewStorage {
    Review createReview(Review review) throws ValidationException;

    Review updateReview(Review review) throws ExistenceException;

    Review removeReviewById(int id) throws ExistenceException;

    Review getReviewById(int id) throws ExistenceException;

    List<Review> getAllReviews(int count);

    List<Review> getAllReviewsByFilmId(Integer filmId, int count);

    void putLike(int id, int userId);

    void putDislike(int id, int userId);

    void removeLike(int id, int userId);

    void removeDislike(int id, int userId);

    void loadReviews(Collection<Film> films);
}
