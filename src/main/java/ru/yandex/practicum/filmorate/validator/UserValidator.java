package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import org.springframework.util.StringUtils;


public class UserValidator {
    public UserValidator() {
    }

    public static void validateUser(User user) throws ValidationException {

            checkEmail(user.getEmail());
            checkLogin(user.getLogin());
            checkBirthday(user.getBirthday());
            if (checkName(user.getName())) {
                user.setName(user.getLogin());
            }
    }

    public static void checkEmail(String email) throws ValidationException {
        if (!StringUtils.hasText(email) || !email.contains("@")) {
            throw new ValidationException("Email не может быть пустым и должен содержать символ @");
        }
    }

    public static void checkLogin(String login) throws ValidationException {
        if (!StringUtils.hasText(login) || StringUtils.containsWhitespace(login)) {
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
    }

    public static boolean checkName(String name) {
        return !StringUtils.hasText(name);
    }

    public static void checkBirthday(LocalDate birthday) throws ValidationException {
            if (birthday.isAfter(LocalDate.now())) {
                throw new ValidationException("Дата рождения не может быть в будущем");
            }
        }
    }
