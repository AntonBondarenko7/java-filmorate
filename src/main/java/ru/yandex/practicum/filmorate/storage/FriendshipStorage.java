package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FriendshipStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendshipStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createFriendship(int id, int friendId) {
        String sqlQuery = "INSERT INTO friendships (user1_id, user2_id, is_approved)" +
                          "VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlQuery, id, friendId, false);
    }

    public boolean deleteFriendship(int id, int friendId) {
        String sqlQuery = "DELETE FROM friendships WHERE user1_id = ? AND user2_id = ?";
        return jdbcTemplate.update(sqlQuery, id, friendId) > 0;
    }

    public List<Friendship> getUserFriendships(int userId) {
        String sqlQuery = "SELECT * FROM friendships WHERE user1_id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFriendship, userId);
    }

    public List<Integer> getCommonFriendIds(int user1Id, int friendId) {
        String sqlQuery = "SELECT user2_id FROM friendships " +
                "WHERE user1_id = ? AND user2_id IN " +
                "(SELECT user2_id FROM friendships WHERE user1_id = ?)";

        return jdbcTemplate.queryForList(sqlQuery, Integer.class, user1Id, friendId);
    }

    public void updateFriendship() {
        String sqlQuery = "UPDATE friendships SET is_approved = true";
        jdbcTemplate.update(sqlQuery);
    }

    private Friendship mapRowToFriendship(ResultSet resultSet, int rowNum) throws SQLException {
        return Friendship.builder()
                .id(resultSet.getInt("id"))
                .user1Id(resultSet.getInt("user1_id"))
                .user2Id(resultSet.getInt("user2_id"))
                .isApproved(resultSet.getBoolean("is_approved"))
                .build();
    }
}
