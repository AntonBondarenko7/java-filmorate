package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class UserValidator {
    public UserValidator() {
    }

    public void validateUser(User user) throws ValidationException {

            checkEmail(user.getEmail());
            checkLogin(user.getLogin());
            checkBirthday(user.getBirthday());
            if (checkName(user.getName())) {
                user.setName(user.getLogin());
            }
    }

    public void checkEmail(String email) throws ValidationException {
        if (email.isBlank() || !email.contains("@")) {
            throw new ValidationException("Email не может быть пустым и должен содержать символ @");
        }
    }

    public void checkLogin(String login) throws ValidationException {
        if (login.isBlank() || login.contains(" ")) {
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
    }

    public boolean checkName(String name) {
        return name == null || name.isBlank();
    }

    public void checkBirthday(LocalDate birthday) throws ValidationException {
            if (birthday.isAfter(LocalDate.now())) {
                throw new ValidationException("Дата рождения не может быть в будущем");
            }
        }
    }
