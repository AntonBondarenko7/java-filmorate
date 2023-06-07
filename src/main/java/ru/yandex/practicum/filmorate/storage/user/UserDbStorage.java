package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

@Repository
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private int id = 0;
    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) throws ValidationException {
        try {
            UserValidator.validateUser(user);
            Integer userId = generateId();
            user.setId(userId);
            String sqlQuery = "insert into users(id, email, login, name, birthday) " +
                              "values (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sqlQuery,
                                userId,
                                user.getEmail(),
                                user.getLogin(),
                                user.getName(),
                                user.getBirthday());
            return user;
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    @Override
    public User updateUser(User user) throws ValidationException, ExistenceException {
        return null;
    }

    @Override
    public int generateId() {
        if (id == 0) {
            return getCurrentMaxId() + 1;
        } else {
            return id + 1;
        }
    }

    @Override
    public HashMap<Integer, User> getAllUsers() {
        return null;
    }

    @Override
    public User getUserById(int userId) throws ValidationException, ExistenceException {
        return null;
    }

    private User mapRowToUser(ResultSet resultSet) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday"))
                .build();
    }

    private int getCurrentMaxId() {
        String sqlQuery = "select max(id) from users";
        if (jdbcTemplate.queryForObject(sqlQuery, Integer.class) == null) {
            return 0;
        } else {
            return jdbcTemplate.queryForObject(sqlQuery, Integer.class);
        }
    }
}
