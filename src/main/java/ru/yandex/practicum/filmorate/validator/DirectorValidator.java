package ru.yandex.practicum.filmorate.validator;

import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

@UtilityClass
public class DirectorValidator {

    public void validateDirector(Director director) {
        if (!StringUtils.hasText(director.getName())) {
            throw new ValidationException("Название режиссера не должно быть пустым");
        }
    }
}
