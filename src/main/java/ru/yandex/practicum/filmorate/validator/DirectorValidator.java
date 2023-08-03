package ru.yandex.practicum.filmorate.validator;

import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

public class DirectorValidator {
    public DirectorValidator() {

    }

    public static void validateDirector(Director director) throws ValidationException {
        if (!StringUtils.hasText(director.getName())) {
            throw new ValidationException("Название режиссера не должно быть пустым");
        }
    }
}
