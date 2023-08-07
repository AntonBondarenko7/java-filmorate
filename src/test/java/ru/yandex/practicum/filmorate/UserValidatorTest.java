package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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
        assertDoesNotThrow(() -> UserValidator.checkBirthday(LocalDate.of(2000, 10, 28)));
    }

    @Test
    void checkBirthdayInTheFuture() {
        assertThrows(ValidationException.class,
                () -> UserValidator.checkBirthday(LocalDate.of(2100, 12, 27)));
    }

    @Test
    void checkNameValid() {
        assertFalse(UserValidator.checkName("John Doe"));
    }

    @Test
    void checkNameInvalid() {
        assertTrue(UserValidator.checkName(""));
    }

    @Test
    void checkLoginValid() {
        assertDoesNotThrow(() -> UserValidator.checkLogin("validUsername"));
    }

    @Test
    void checkLoginInvalidEmpty() {
        assertThrows(ValidationException.class, () -> UserValidator.checkLogin(""));
    }

    @Test
    void checkLoginInvalidWithSpace() {
        assertThrows(ValidationException.class, () -> UserValidator.checkLogin("invalid username"));
    }

    @Test
    void checkEmailValid() {
        assertDoesNotThrow(() -> UserValidator.checkEmail("valid@example.com"));
    }

    @Test
    void checkEmailInvalidEmpty() {
        assertThrows(ValidationException.class, () -> UserValidator.checkEmail(""));
    }

    @Test
    void checkEmailInvalidNoAtSymbol() {
        assertThrows(ValidationException.class, () -> UserValidator.checkEmail("invalid.example.com"));
    }
}
