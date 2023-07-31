package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikeStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createLike(int userId, int filmId) {
        String sqlQuery = "INSERT INTO likes (user_id, film_id)" +
                "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    public boolean deleteLike(int userId, int filmId) {
        String sqlQuery = "DELETE FROM likes " +
                "WHERE user_id = ? AND film_id = ?";
        return jdbcTemplate.update(sqlQuery, userId, filmId) > 0;
    }

    public boolean deleteAllLikes() {
        String sqlQuery = "DELETE FROM likes";
        return jdbcTemplate.update(sqlQuery) > 0;
    }

    public List<Like> getLikesByUserId(int userId) {
        String sqlQuery = "SELECT * FROM likes WHERE user_id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToLike, userId);
    }

    public List<Like> getLikesByFilmId(int filmId) {
        String sqlQuery = "SELECT * FROM likes WHERE film_id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToLike, filmId);
    }

    private Like mapRowToLike(ResultSet resultSet, int rowNum) throws SQLException {
        return Like.builder()
                .id(resultSet.getInt("id"))
                .userId(resultSet.getInt("user_id"))
                .filmId(resultSet.getInt("film_id"))
                .build();
    }
}
