package ru.yandex.practicum.filmorate.validator;


import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FilmValidator {

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static String START_DATE = "1895-12-28";
    private static final LocalDate date = LocalDate.parse(START_DATE, formatter);

    public FilmValidator() {

    }

    public static void validateFilm(Film film) throws ValidationException {
        checkFilmName(film.getName());
        checkDescription(film.getDescription());
        checkReleaseDate(film.getReleaseDate());
        checkDuration(film.getDuration());
    }

    public static void checkFilmName(String name) throws ValidationException {
        if (!StringUtils.hasText(name)) {
            throw new ValidationException("Название фильма не должно быть пустым");
        }
    }

    public static void checkDescription(String description) throws ValidationException {
        if (description.length() > 200) {
            throw new ValidationException("Длина описания не должна быть >200 символов");
        }
    }

    public static void checkReleaseDate(LocalDate releaseDate) throws ValidationException {
        try {
            if (releaseDate.isBefore(date)) {
                throw new ValidationException("Дата создания фильма должна быть не ранее 1895-12-28");
            }
        } catch (DateTimeParseException e) {
            throw new ValidationException("Неверный формат даты релиза фильма (ожидается гггг-мм-дд)");
        }
    }

    public static void checkDuration(int duration) throws ValidationException {
        if (duration < 0) {
            throw new ValidationException("Продолжительность фильма не может быть отрицательной");
        }
    }
}
