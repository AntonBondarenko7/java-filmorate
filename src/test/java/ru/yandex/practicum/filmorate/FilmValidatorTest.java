package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidatorTest {
    private FilmValidator filmValidator;

    @BeforeEach
    void setUp() {
        filmValidator = new FilmValidator();
    }

    @Test
    void checkFilmNameValid() {
        assertDoesNotThrow(() -> filmValidator.checkFilmName("Valid Film Name"));
    }

    @Test
    void checkFilmNameInvalid() {
        assertThrows(ValidationException.class, () -> filmValidator.checkFilmName(""));
    }

    @Test
    void checkDescriptionValid() {
        assertDoesNotThrow(() -> filmValidator.checkDescription("This is a valid description."));
    }

    @Test
    void checkDescriptionInvalid() {
        String invalidDescription = "A".repeat(201);
        assertThrows(ValidationException.class, () -> filmValidator.checkDescription(invalidDescription));
    }

    @Test
    void checkDescriptionBorder() {
        String validDescription = "A".repeat(200);
        assertDoesNotThrow(() -> filmValidator.checkDescription(validDescription));
    }

    @Test
    void checkReleaseDateValid() {
        assertDoesNotThrow(
                () -> filmValidator.checkReleaseDate(LocalDate.of(2000, 6, 12)));
    }

    @Test
    void checkReleaseDateBeforeStart() {
        assertThrows(ValidationException.class,
                () -> filmValidator.checkReleaseDate(LocalDate.of(1895, 12, 27)));
    }

    @Test
    void checkReleaseDateBorder() {
        assertDoesNotThrow(() -> filmValidator.checkReleaseDate(LocalDate.of(1895, 12, 28)));
    }

    @Test
    void checkDurationValid() {
        assertDoesNotThrow(() -> filmValidator.checkDuration(120));
    }

    @Test
    void checkDurationInvalid() {
        assertThrows(ValidationException.class, () -> filmValidator.checkDuration(-1));
    }

    @Test
    void checkDurationZero() {
        assertDoesNotThrow(() -> filmValidator.checkDuration(0));
    }
}