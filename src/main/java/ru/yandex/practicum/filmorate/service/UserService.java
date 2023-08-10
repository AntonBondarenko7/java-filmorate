package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventOperation;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;
    private final EventService eventService;

    public void addFriend(int id, int friendId) throws ValidationException, ExistenceException {
        userStorage.getUserById(id);
        userStorage.getUserById(friendId);
        friendshipStorage.createFriendship(id, friendId);
        eventService.addEvent(new Event(
                0,
                System.currentTimeMillis(),
                id,
                EventType.FRIEND,
                EventOperation.ADD,
                friendId));
    }

    public void removeFriend(int id, int friendId) throws ValidationException, ExistenceException {
        userStorage.getUserById(id);
        userStorage.getUserById(friendId);
        friendshipStorage.deleteFriendship(id, friendId);
        eventService.addEvent(new Event(
                0,
                System.currentTimeMillis(),
                id,
                EventType.FRIEND,
                EventOperation.REMOVE,
                friendId));
    }

    public ArrayList<User> getCommonFriends(int id, int friendId) throws ValidationException, ExistenceException {
        ArrayList<User> commonFriends = new ArrayList<>();
        ArrayList<Integer> commonFriendsIds = new ArrayList<>(friendshipStorage.getCommonFriendIds(id, friendId));
        for (Integer i : commonFriendsIds) {
            commonFriends.add(userStorage.getUserById(i));
        }
        return commonFriends;
    }

    public ArrayList<User> getUserFriends(int userId) throws ValidationException, ExistenceException {
        ArrayList<User> friends = new ArrayList<>();

        if (getUserById(userId) != null) {
            ArrayList<Friendship> friendships = new ArrayList<>(friendshipStorage.getUserFriendships(userId));
            for (Friendship f : friendships) {
                User user = getUserById(f.getUser2Id());
                friends.add(user);
            }
        }
        return friends;
    }

    private ArrayList<Integer> getUserFriendsIds(int userId) {
        ArrayList<Integer> friends = new ArrayList<>();
        ArrayList<Friendship> friendships = new ArrayList<>(friendshipStorage.getUserFriendships(userId));
        for (Friendship f : friendships) {
            friends.add(f.getUser2Id());
        }
        return friends;
    }

    public User createUser(User user) {
        try {
            UserValidator.validateUser(user);
            return userStorage.createUser(user);
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    public User updateUser(User user) {
        try {
            UserValidator.validateUser(user);
            return userStorage.updateUser(user);
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    public Collection<User> getAllUsers() {
        Collection<User> users = userStorage.getAllUsers().values();
        for (User u : users) {
            u.setFriends(getUserFriendsIds(u.getId()));
        }
        return users;
    }

    public User getUserById(int userId) throws ValidationException, ExistenceException {
        User user = userStorage.getUserById(userId);
        user.setFriends(getUserFriendsIds(userId));
        return user;
    }

    public void removeUserById(int id) throws ValidationException {
        if (id <= 0) {
            throw new ValidationException("id должен быть положительным");
        }
        userStorage.removeUserById(id);
    }
}
