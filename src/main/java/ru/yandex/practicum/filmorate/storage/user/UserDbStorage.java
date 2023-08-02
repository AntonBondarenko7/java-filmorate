package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
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

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) throws ValidationException, ExistenceException {
        try {
            UserValidator.validateUser(user);
            String sqlQuery = "INSERT INTO users(email, login, name, birthday) " +
                              "VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sqlQuery,
//                                userId,
                                user.getEmail(),
                                user.getLogin(),
                                user.getName(),
                                user.getBirthday());
            return getUserByLoginAndEmail(user.getLogin(), user.getEmail());
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        } catch (ExistenceException e) {
            throw new ExistenceException("Ошибка при создании пользователя");
        } catch (DataAccessException e) {
            throw new ValidationException("Пользователь с таким логином и/или email уже существует");
        }
    }

    @Override
    public User updateUser(User user) throws ValidationException, ExistenceException {
        String sqlQuery = "SELECT * FROM users WHERE id = ?";
        try {
            jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, user.getId());
        } catch (EmptyResultDataAccessException e) {
            String errorMessage = "Пользователя с таким идентификатором нет в списке";
            throw new ExistenceException(errorMessage);
        }

        try {
            UserValidator.validateUser(user);
            sqlQuery = "UPDATE users SET " +
                    "email = ?, login = ?, name = ?, birthday = ?" +
                    "WHERE id = ?";
            jdbcTemplate.update(sqlQuery,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId());
            return getUserById(user.getId());
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        } catch (DataAccessException e) {
            throw new ValidationException("Пользователь с таким логином и/или email уже существует");
        }
    }

    @Override
    public HashMap<Integer, User> getAllUsers() {
        String sqlQuery = "SELECT * FROM users";
        List<User> userList = jdbcTemplate.query(sqlQuery, this::mapRowToUser);

        HashMap<Integer, User> userMap = new HashMap<>();
        for (User user : userList) {
            userMap.put(user.getId(), user);
        }

        return userMap;
    }

    public User getUserByLoginAndEmail(String login, String email) throws ExistenceException {
        String sqlQuery = "SELECT * from users WHERE login = ? AND email = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, login, email);
        } catch (EmptyResultDataAccessException e) {
            String errorMessage = "Пользователя с такими данными нет в списке";
            throw new ExistenceException(errorMessage);
        }
    }

    @Override
    public User getUserById(int userId) throws ExistenceException {
        String sqlQuery = "SELECT * from users WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, userId);
        } catch (EmptyResultDataAccessException e) {
            String errorMessage = "Пользователя с таким идентификатором нет в списке";
            throw new ExistenceException(errorMessage);
        }
    }

    @Override
    public void removeUserById(int id) {
        String sqlQuery = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sqlQuery, id);
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
}
