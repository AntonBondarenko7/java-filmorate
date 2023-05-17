package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.Collection;
import java.util.HashMap;


@RestController
@RequestMapping("/users")
public class UserController {
    private final InMemoryUserStorage userStorage;

    @Autowired
    public UserController(InMemoryUserStorage userStorage) {this.userStorage = userStorage;}

    @GetMapping
    public Collection<User> getAllUsers() {
        return userStorage.getAllUsersValues();
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) throws ValidationException {
        userStorage.createUser(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user) throws ValidationException {
        userStorage.updateUser(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
