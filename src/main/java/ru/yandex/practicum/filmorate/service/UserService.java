package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public void addFriend(int id, int friendId) throws ValidationException, ExistenceException {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public void removeFriend(int id, int friendId) throws ValidationException, ExistenceException {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public ArrayList<User> getCommonFriends(int id, int friendId) throws ValidationException, ExistenceException {
        ArrayList<User> commonFriends = new ArrayList<>();
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        Set<Integer> userFriends = new HashSet<>(user.getFriends());
        Set<Integer> friendFriends = new HashSet<>(friend.getFriends());
        userFriends.retainAll(friendFriends);
        for (Integer i : userFriends) {
            commonFriends.add(userStorage.getUserById(i));
        }
        return commonFriends;
    }

    public ArrayList<User> getUserFriends(int userId) throws ValidationException, ExistenceException {
        ArrayList<User> friends = new ArrayList<>();
        for (int id : userStorage.getUserById(userId).getFriends()) {
            friends.add(userStorage.getUserById(id));
        }
        return friends;
    }

    public User createUser(User user) throws ValidationException {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) throws ValidationException, ExistenceException {
        return userStorage.updateUser(user);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers().values();
    }

    public User getUserById(int userId) throws ValidationException, ExistenceException {
        return userStorage.getUserById(userId);
    }
}
