package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmValidatorTest {

    @Test
    void checkFilmNameValid() {
        assertDoesNotThrow(() -> FilmValidator.checkFilmName("Valid Film Name"));
    }

    @Test
    void checkFilmNameInvalid() {
        assertThrows(ValidationException.class, () -> FilmValidator.checkFilmName(""));
    }

    @Test
    void checkDescriptionValid() {
        assertDoesNotThrow(() -> FilmValidator.checkDescription("This is a valid description."));
    }

    @Test
    void checkDescriptionInvalid() {
        String invalidDescription = "A".repeat(201);
        assertThrows(ValidationException.class, () -> FilmValidator.checkDescription(invalidDescription));
    }

    @Test
    void checkDescriptionBorder() {
        String validDescription = "A".repeat(200);
        assertDoesNotThrow(() -> FilmValidator.checkDescription(validDescription));
    }

    @Test
    void checkReleaseDateValid() {
        assertDoesNotThrow(
                () -> FilmValidator.checkReleaseDate(LocalDate.of(2000, 6, 12)));
    }

    @Test
    void checkReleaseDateBeforeStart() {
        assertThrows(ValidationException.class,
                () -> FilmValidator.checkReleaseDate(LocalDate.of(1895, 12, 27)));
    }

    @Test
    void checkReleaseDateBorder() {
        assertDoesNotThrow(() -> FilmValidator.checkReleaseDate(LocalDate.of(1895, 12, 28)));
    }

    @Test
    void checkDurationValid() {
        assertDoesNotThrow(() -> FilmValidator.checkDuration(120));
    }

    @Test
    void checkDurationInvalid() {
        assertThrows(ValidationException.class, () -> FilmValidator.checkDuration(-1));
    }

    @Test
    void checkDurationZero() {
        assertDoesNotThrow(() -> FilmValidator.checkDuration(0));
    }
}