package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User createUser(User user) {
        try {
            String sqlQuery = "INSERT INTO users(email, login, name, birthday) " +
                    "VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sqlQuery,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday());
            return getUserByLoginAndEmail(user.getLogin(), user.getEmail());
        } catch (ExistenceException e) {
            throw new ExistenceException("Ошибка при создании пользователя");
        } catch (DataAccessException e) {
            throw new ValidationException("Пользователь с таким логином и/или email уже существует");
        }
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "SELECT * FROM users WHERE id = ?";
        try {
            jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, user.getId());
        } catch (EmptyResultDataAccessException e) {
            String errorMessage = "Пользователя с таким идентификатором нет в списке";
            throw new ExistenceException(errorMessage);
        }

        try {
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

    public User getUserByLoginAndEmail(String login, String email) {
        String sqlQuery = "SELECT * from users WHERE login = ? AND email = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, login, email);
        } catch (EmptyResultDataAccessException e) {
            String errorMessage = "Пользователя с такими данными нет в списке";
            throw new ExistenceException(errorMessage);
        }
    }

    @Override
    public User getUserById(int userId) {
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

    @Override
    public List<User> getUserFriends(int userId) {
        String sqlQuery = "SELECT u.*\n" +
                          "FROM users u\n" +
                          "INNER JOIN friendships f on u.id = f.user2_id\n" +
                          "WHERE f.user1_id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId);
    }

    @Override
    public List<User> getCommonFriends(int user1Id, int friendId) {
        String sqlQuery = "SELECT u.* \n" +
                "FROM users u \n" +
                "INNER JOIN friendships f on u.id = f.user2_id \n" +
                "WHERE f.user1_id = ? AND f.user2_id IN \n" +
                "(SELECT f2.user2_id FROM friendships f2 WHERE f2.user1_id = ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, user1Id, friendId);
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
