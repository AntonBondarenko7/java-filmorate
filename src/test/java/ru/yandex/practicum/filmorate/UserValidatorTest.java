package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {
    private UserValidator userValidator;

    @BeforeEach
    void setUp() {
        userValidator = new UserValidator();
    }

    @Test
    void checkBirthdayValid() {
        assertDoesNotThrow(() -> userValidator.checkBirthday(LocalDate.of(2000, 10, 28)));
    }

    @Test
    void checkBirthdayInTheFuture() {
        assertThrows(ValidationException.class,
                () -> userValidator.checkBirthday(LocalDate.of(2100, 12, 27)));
    }

    @Test
    void checkNameValid() {
        assertFalse(userValidator.checkName("John Doe"));
    }

    @Test
    void checkNameInvalid() {
        assertTrue(userValidator.checkName(""));
    }

    @Test
    void checkLoginValid() {
        assertDoesNotThrow(() -> userValidator.checkLogin("validUsername"));
    }

    @Test
    void checkLoginInvalidEmpty() {
        assertThrows(ValidationException.class, () -> userValidator.checkLogin(""));
    }

    @Test
    void checkLoginInvalidWithSpace() {
        assertThrows(ValidationException.class, () -> userValidator.checkLogin("invalid username"));
    }

    @Test
    void checkEmailValid() {
        assertDoesNotThrow(() -> userValidator.checkEmail("valid@example.com"));
    }

    @Test
    void checkEmailInvalidEmpty() {
        assertThrows(ValidationException.class, () -> userValidator.checkEmail(""));
    }

    @Test
    void checkEmailInvalidNoAtSymbol() {
        assertThrows(ValidationException.class, () -> userValidator.checkEmail("invalid.example.com"));
    }
}
