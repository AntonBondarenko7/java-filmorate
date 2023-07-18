package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@Primary
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
            int userId = generateId();
            user.setId(userId);
            String sqlQuery = "INSERT INTO \"users\"(\"id\", \"email\", \"login\", \"name\", \"birthday\") " +
                              "VALUES (?, ?, ?, ?, ?)";
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
        String sqlQuery = "SELECT * FROM \"users\" WHERE \"id\" = ?";
        try {
            jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, user.getId());
        } catch (EmptyResultDataAccessException e) {
            String errorMessage = "Пользователя с таким идентификатором нет в списке";
            throw new ExistenceException(errorMessage);
        }

        try {
            UserValidator.validateUser(user);
            sqlQuery = "UPDATE \"users\" SET " +
                    "\"email\" = ?, \"login\" = ?, \"name\" = ?, \"birthday\" = ?" +
                    "WHERE \"id\" = ?";
            jdbcTemplate.update(sqlQuery,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId());
            return user;
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    @Override
    public int generateId() {
        if (id == 0) {
            id = getCurrentMaxId() + 1;
        } else {
            id++;
        }
        return id;
    }

    @Override
    public HashMap<Integer, User> getAllUsers() {
        String sqlQuery = "SELECT * FROM \"users\"";
        List<User> userList = jdbcTemplate.query(sqlQuery, this::mapRowToUser);

        HashMap<Integer, User> userMap = new HashMap<>();
        for (User user : userList) {
            userMap.put(user.getId(), user);
        }

        return userMap;
    }

    @Override
    public User getUserById(int userId) throws ExistenceException {
        String sqlQuery = "SELECT * from \"users\" WHERE \"id\" = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, userId);
        } catch (EmptyResultDataAccessException e) {
            String errorMessage = "Пользователя с таким идентификатором нет в списке";
            throw new ExistenceException(errorMessage);
        }
    }

    public boolean deleteUserById(int id) {
        String sqlQuery = "DELETE FROM \"users\"" +
                "WHERE \"id\" = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }

    private int getCurrentMaxId() {
        String sqlQuery = "SELECT max(\"id\") FROM  \"users\"";
        if (jdbcTemplate.queryForObject(sqlQuery, Integer.class) == null) {
            return 0;
        } else {
            return jdbcTemplate.queryForObject(sqlQuery, Integer.class);
        }
    }
}
