package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
@Service
public class UserService {
    private final UserStorage userStorage;
    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

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
        for (Integer i : userFriends) {
            if (friendFriends.contains(i)) {
                commonFriends.add(userStorage.getUserById(i));
            }
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
}
