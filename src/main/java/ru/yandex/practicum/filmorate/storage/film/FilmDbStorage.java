package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.TreeSet;

@Primary
@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private int id = 0;
    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film createFilm(Film film) throws ValidationException {
        try {
            FilmValidator.validateFilm(film);
            Integer filmId = generateId();
            film.setId(filmId);
            String sqlQuery = "INSERT INTO \"films\"(\"id\", \"name\", \"description\", \"release_date\", " +
                    "\"duration\") VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sqlQuery,
                    filmId,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration());
            return film;
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException, ExistenceException {
        return null;
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
    public HashMap<Integer, Film> getAllFilms() {
        return null;
    }

    @Override
    public int getFilmLikesCount(Film film) {
        return 0;
    }

    @Override
    public TreeSet<Film> getMostPopularFilms() {
        return null;
    }

    @Override
    public Film getFilmById(int filmId) throws ValidationException, ExistenceException {
        return null;
    }

    private int getCurrentMaxId() {
        String sqlQuery = "SELECT max(\"id\") FROM  \"films\"";
        if (jdbcTemplate.queryForObject(sqlQuery, Integer.class) == null) {
            return 0;
        } else {
            return jdbcTemplate.queryForObject(sqlQuery, Integer.class);
        }
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .build();
    }
}
