package ru.yandex.practicum.filmorate.validator;

import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;

public class ReviewValidator {

    public static void validateReview(Review review) throws ValidationException {
        if (!StringUtils.hasText(review.getContent())) {
            throw new ValidationException("Отзыв не должен быть пустым");
        }
        if (review.getIsPositive() == null) {
            throw new ValidationException("Необходимо указать тип отзыва");
        }
    }
}
