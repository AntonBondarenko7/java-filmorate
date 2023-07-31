package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.HashMap;

public interface UserStorage {
    User createUser(User user) throws ValidationException, ExistenceException;

    User updateUser(User user) throws ValidationException, ExistenceException;

    default int generateId() {
        throw new UnsupportedOperationException("Метод не поддерживается");
    }

    HashMap<Integer, User> getAllUsers();

    User getUserById(int userId) throws ValidationException, ExistenceException;
}
