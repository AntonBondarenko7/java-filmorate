package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.Collection;
import java.util.HashMap;


@RestController
@RequestMapping("/users")
public class UserController {
    int id = 0;
    private final HashMap<Integer, User> users = new HashMap<>();
    UserValidator validator = new UserValidator();

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) throws ValidationException {
        validator.validateUser(user);
        Integer userId = generateId();
        user.setId(userId);
        users.put(userId, user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user) throws ValidationException {
        if (users.containsKey(user.getId())) {
            validator.validateUser(user);
            users.put(user.getId(), user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            throw new ValidationException("Пользователя с таким идентификатором нет в списке");
        }
    }

    private int generateId() {
        id++;
        return id;
    }
}
