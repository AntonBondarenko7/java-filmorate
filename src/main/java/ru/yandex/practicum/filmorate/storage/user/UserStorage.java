package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;

public interface UserStorage {
    public User createUser(User user) throws ValidationException;
    public User updateUser(User user) throws ValidationException;
    public Collection<User> getAllUsersValues();
    public int generateId();
    public HashMap<Integer, User> getAllUsers();
}
