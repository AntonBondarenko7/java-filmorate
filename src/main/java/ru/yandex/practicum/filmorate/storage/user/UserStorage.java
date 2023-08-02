package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.HashMap;

public interface UserStorage {
    User createUser(User user) throws ValidationException, ExistenceException;

    User updateUser(User user) throws ValidationException, ExistenceException;

    HashMap<Integer, User> getAllUsers();

    User getUserById(int userId) throws ValidationException, ExistenceException;

    void removeUserById(int id);
}
