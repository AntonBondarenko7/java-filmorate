package ru.yandex.practicum.filmorate.validator;

import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;

@UtilityClass
public class ReviewValidator {

    public void validateReview(Review review) {
        if (!StringUtils.hasText(review.getContent())) {
            throw new ValidationException("Отзыв не должен быть пустым");
        }
        if (review.getIsPositive() == null) {
            throw new ValidationException("Необходимо указать тип отзыва");
        }
    }
}
