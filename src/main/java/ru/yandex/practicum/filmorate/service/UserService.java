package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventOperation;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;
    private final EventService eventService;

    public void addFriend(int id, int friendId) {
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

    public void removeFriend(int id, int friendId) {
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

    public List<User> getCommonFriends(int id, int friendId) {
        getUserById(id);
        getUserById(friendId);
        return userStorage.getCommonFriends(id, friendId);
    }

    public List<User> getUserFriends(int userId) {
        getUserById(userId);
        return userStorage.getUserFriends(userId);
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
        return userStorage.getAllUsers().values();
    }

    public User getUserById(int userId) {
        return userStorage.getUserById(userId);
    }

    public void removeUserById(int id) {
        if (id <= 0) {
            throw new ValidationException("id должен быть положительным");
        }
        userStorage.removeUserById(id);
    }
}
