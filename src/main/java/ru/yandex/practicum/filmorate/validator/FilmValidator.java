package ru.yandex.practicum.filmorate.validator;


import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@UtilityClass
public class FilmValidator {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    final static String START_DATE = "1895-12-28";
    private final LocalDate date = LocalDate.parse(START_DATE, formatter);

    public void validateFilm(Film film) {
        checkFilmName(film.getName());
        checkDescription(film.getDescription());
        checkReleaseDate(film.getReleaseDate());
        checkDuration(film.getDuration());
    }

    public void checkFilmName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new ValidationException("Название фильма не должно быть пустым");
        }
    }

    public void checkDescription(String description) {
        if (description.length() > 200) {
            throw new ValidationException("Длина описания не должна быть >200 символов");
        }
    }

    public void checkReleaseDate(LocalDate releaseDate) {
        try {
            if (releaseDate.isBefore(date)) {
                throw new ValidationException("Дата создания фильма должна быть не ранее 1895-12-28");
            }
        } catch (DateTimeParseException e) {
            throw new ValidationException("Неверный формат даты релиза фильма (ожидается гггг-мм-дд)");
        }
    }

    public void checkDuration(int duration) {
        if (duration < 0) {
            throw new ValidationException("Продолжительность фильма не может быть отрицательной");
        }
    }
}
