package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MpaRatingStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaRatingStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createMpaRating(String name, String description) {
        String sqlQuery = "INSERT INTO \"mpa_ratings\" (\"name\", \"description\")" +
                "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery,name, description, false);
    }

    public boolean deleteMpaRating(int id) {
        String sqlQuery = "DELETE FROM \"mpa_ratings\"" +
                "WHERE \"id\" = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    public List<MpaRating> getAllMpaRatings() {
        String sqlQuery = "SELECT * FROM \"mpa_ratings\"";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpaRating);
    }

    public MpaRating getMpaRatingById(int id) throws ExistenceException {
        String sqlQuery = "SELECT * FROM \"mpa_ratings\"" +
                "WHERE \"id\" = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpaRating, id);
        } catch (EmptyResultDataAccessException e) {
            String errorMessage = "Рейтинга с таким идентификатором нет в списке";
            throw new ExistenceException(errorMessage);
        }
    }

    public MpaRating updateMpaRating(MpaRating mpaRating) throws ExistenceException {
        String sqlQuery = "SELECT * FROM \"mpa_ratings\"" +
                "WHERE \"id\" = ?";
        try {
            jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpaRating, mpaRating.getId());
        } catch (EmptyResultDataAccessException e) {
            String errorMessage = "Рейтинга с таким идентификатором нет в списке";
            throw new ExistenceException(errorMessage);
        }

        sqlQuery = "UPDATE \"mpa_ratings\" SET \"name\" = ?, \"description\" = ? WHERE \"id\" = ?";
        jdbcTemplate.update(sqlQuery, mpaRating.getName(), mpaRating.getDescription(), mpaRating.getId());
        return mpaRating;
    }

    private MpaRating mapRowToMpaRating(ResultSet resultSet, int rowNum) throws SQLException {
        return MpaRating.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .build();
    }
}
