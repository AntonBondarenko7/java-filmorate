package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    public void addFriend(int id, int friendId) throws ValidationException, ExistenceException {
        userStorage.getUserById(id);
        userStorage.getUserById(friendId);
        friendshipStorage.createFriendship(id, friendId);
    }

    public void removeFriend(int id, int friendId) throws ValidationException, ExistenceException {
        userStorage.getUserById(id);
        userStorage.getUserById(friendId);
        friendshipStorage.deleteFriendship(id, friendId);
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
        ArrayList<Friendship> friendships = new ArrayList<>(friendshipStorage.getUserFriendships(userId));
        for (Friendship f : friendships) {
            User user = getUserById(f.getUser2Id());
            friends.add(user);
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

    public User createUser(User user) throws ValidationException {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) throws ValidationException, ExistenceException {
        return userStorage.updateUser(user);
    }

    public Collection<User> getAllUsers() {
        Collection<User> users = userStorage.getAllUsers().values();
        for (User u: users) {
            u.setFriends(getUserFriendsIds(u.getId()));
        }
        return users;
    }

    public User getUserById(int userId) throws ValidationException, ExistenceException {
        User user = userStorage.getUserById(userId);
        user.setFriends(getUserFriendsIds(userId));
        return user;
    }
}
