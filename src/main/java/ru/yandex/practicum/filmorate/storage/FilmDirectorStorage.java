package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.FilmDirector;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class FilmDirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DirectorDbStorage directorDbStorage;

    @Autowired
    public FilmDirectorStorage(JdbcTemplate jdbcTemplate,
                               DirectorDbStorage directorDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.directorDbStorage = directorDbStorage;
    }

    public void createFilmDirector(int filmId, int directorid) throws ExistenceException, ValidationException {
        directorDbStorage.getDirectorById(directorid);
        String sqlQuery = "INSERT INTO directors_films (film_id, director_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, directorid);
    }

    public boolean deleteAllFilmDirectorsByFilmId(int filmId) {
        String sqlQuery = "DELETE FROM directors_films WHERE film_id = ?";
        return jdbcTemplate.update(sqlQuery, filmId) > 0;
    }

    public Set<Director> getFilmDirectorsByFilmId(int filmId) throws ExistenceException {
        String sqlQuery = "SELECT * FROM directors_films WHERE film_id = ? ORDER BY director_id ASC";
        List<FilmDirector> fdlist = jdbcTemplate.query(sqlQuery, this::mapRowToFilmDirector, filmId);
        Set<Director> dSet = new HashSet<>();
        for (FilmDirector fd : fdlist) {
            dSet.add(directorDbStorage.getDirectorById(fd.getDirectorId()));
        }
        return dSet;
    }

    private FilmDirector mapRowToFilmDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return FilmDirector.builder()
                .filmId(resultSet.getInt("film_id"))
                .directorId(resultSet.getInt("director_id"))
                .build();
    }
}
