package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createGenre(String name) {
        String sqlQuery = "INSERT INTO genres (name) VALUES (?)";
        jdbcTemplate.update(sqlQuery, name);
    }

    public boolean deleteGenre(int id) {
        String sqlQuery = "DELETE FROM genres WHERE id = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    public boolean deleteAllGenres() {
        String sqlQuery = "DELETE FROM genres";
        return jdbcTemplate.update(sqlQuery) > 0;
    }

    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT * FROM genres";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    public Genre getGenreById(int id) throws ExistenceException {
        String sqlQuery = "SELECT * FROM genres WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
        } catch (EmptyResultDataAccessException e) {
            String errorMessage = "Жанра с таким идентификатором нет в списке";
            throw new ExistenceException(errorMessage);
        }
    }

    public Genre updateGenre(Genre genre) throws ExistenceException {
        String sqlQuery = "SELECT * FROM genres WHERE id = ?";
        try {
            jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, genre.getId());
        } catch (EmptyResultDataAccessException e) {
            String errorMessage = "Жанра с таким идентификатором нет в списке";
            throw new ExistenceException(errorMessage);
        }

        sqlQuery = "UPDATE genres SET name = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, genre.getName(), genre.getId());
        return genre;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
