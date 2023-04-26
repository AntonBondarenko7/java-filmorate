package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) throws ValidationException {
        try {
            UserValidator.validateUser(user);
            Integer userId = generateId();
            user.setId(userId);
            users.put(userId, user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (ValidationException e) {
            log.error(e.getMessage(), e);
            throw new ValidationException(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user) throws ValidationException {
        if (users.containsKey(user.getId())) {
            try {
                UserValidator.validateUser(user);
                users.put(user.getId(), user);
                return new ResponseEntity<>(user, HttpStatus.OK);
            } catch (ValidationException e) {
                log.error(e.getMessage(), e);
                throw new ValidationException(e.getMessage());
            }
        } else {
            String errorMessage = "Пользователя с таким идентификатором нет в списке";
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    private int generateId() {
        id++;
        return id;
    }
}
