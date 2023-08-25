package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    HashMap<Integer, User> getAllUsers();

    User getUserById(int userId);

    List<User> getUserFriends(int userId);

    List<User> getCommonFriends(int user1Id, int friendId);

    void removeUserById(int id);
}
