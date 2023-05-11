package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.Collection;
import java.util.HashMap;

@Component
public class InMemoryUserStorage implements UserStorage {
    private int id = 0;
    private final HashMap<Integer, User> users = new HashMap<>();

    @Override
    public User createUser(User user) throws ValidationException {
        try {
            UserValidator.validateUser(user);
            Integer userId = generateId();
            user.setId(userId);
            users.put(userId, user);
            return user;
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    @Override
    public User updateUser(User user) throws ValidationException {
        if (users.containsKey(user.getId())) {
            try {
                UserValidator.validateUser(user);
                users.put(user.getId(), user);
                return user;
            } catch (ValidationException e) {
                throw new ValidationException(e.getMessage());
            }
        } else {
            String errorMessage = "Пользователя с таким идентификатором нет в списке";
            throw new ValidationException(errorMessage);
        }
    }

    @Override
    public Collection<User> getAllUsersValues() {
        return users.values();
    }

    @Override
    public int generateId() {
        id++;
        return id;
    }

    @Override
    public HashMap<Integer, User> getAllUsers() {
        return users;
    }
}
