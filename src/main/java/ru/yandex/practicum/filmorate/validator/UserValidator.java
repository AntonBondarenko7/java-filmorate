package ru.yandex.practicum.filmorate.validator;

import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@UtilityClass
public class UserValidator {

    public void validateUser(User user) {
        checkEmail(user.getEmail());
        checkLogin(user.getLogin());
        checkBirthday(user.getBirthday());
        if (checkName(user.getName())) {
            user.setName(user.getLogin());
        }
    }

    public void checkEmail(String email) {
        if (!StringUtils.hasText(email) || !email.contains("@")) {
            throw new ValidationException("Email не может быть пустым и должен содержать символ @");
        }
    }

    public void checkLogin(String login) {
        if (!StringUtils.hasText(login) || StringUtils.containsWhitespace(login)) {
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
    }

    public boolean checkName(String name) {
        return !StringUtils.hasText(name);
    }

    public void checkBirthday(LocalDate birthday) {
        if (birthday != null && birthday.isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
