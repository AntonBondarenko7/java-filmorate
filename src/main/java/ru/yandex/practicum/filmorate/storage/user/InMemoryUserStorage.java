package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;
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
    public User updateUser(User user) throws ValidationException, ExistenceException {
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
            throw new ExistenceException(errorMessage);
        }
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

    @Override
    public User getUserById(int userId) throws ExistenceException {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            String errorMessage = "Пользователя с таким идентификатором нет в списке";
            throw new ExistenceException(errorMessage);
        }
    }
}
