package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Primary
@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film createFilm(Film film) throws ValidationException {
        try {
            FilmValidator.validateFilm(film);
            String sqlQuery = "INSERT INTO films (name, description, release_date, " +
                    "duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sqlQuery,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId());
            return getFilmByNameAndReleaseDate(film.getName(), film.getReleaseDate(), film.getDuration());
        } catch (ValidationException | ExistenceException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException, ExistenceException {
        getFilmById(film.getId());
        try {
            FilmValidator.validateFilm(film);
            String sqlQuery = "UPDATE films SET " +
                    "name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ?" +
                    "WHERE id = ?";
            jdbcTemplate.update(sqlQuery,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());
            return getFilmById(film.getId());
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    @Override
    public HashMap<Integer, Film> getAllFilms() {
        String sqlQuery = "SELECT f.id,  f.name, f.description, f.release_date, f.duration, f.mpa_rating_id, " +
                "mr.name AS mpa_name, mr.description AS mpa_description\n" +
                "FROM films f\n" +
                "LEFT JOIN mpa_ratings mr ON mr.id = f.mpa_rating_id";
        List<Film> filmList = jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> {
            try {
                return mapRowToFilm(resultSet, rowNum);
            } catch (ExistenceException e) {
                throw new RuntimeException(e);
            }
        });

        HashMap<Integer, Film> filmMap = new HashMap<>();
        for (Film film : filmList) {
            filmMap.put(film.getId(), film);
        }

        return filmMap;
    }

    public Film getFilmByNameAndReleaseDate(String name, LocalDate releaseDate, int duration) throws ExistenceException {
        String sqlQuery = "SELECT f.id,  f.name, f.description, f.release_date, f.duration, f.mpa_rating_id, " +
                "mr.name AS mpa_name, mr.description AS mpa_description\n" +
                "FROM films f\n" +
                "LEFT JOIN mpa_ratings mr ON mr.id = f.mpa_rating_id\n" +
                "WHERE f.name = ? AND f.release_date = ? AND f.duration = ? ";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, (resultSet, rowNum) -> {
                try {
                    return mapRowToFilm(resultSet, rowNum);
                } catch (ExistenceException e) {
                    throw new RuntimeException(e);
                }
            }, name, releaseDate, duration);
        } catch (EmptyResultDataAccessException e) {
            String errorMessage = "Фильма с такими данными нет в списке";
            throw new ExistenceException(errorMessage);
        }
    }

    @Override
    public List<Film> getMostPopularFilms(int count) throws ExistenceException {
        String sqlQuery = "SELECT f.id,  f.name, f.description, f.release_date, f.duration, f.mpa_rating_id, " +
                "mr.name AS mpa_name, mr.description AS mpa_description\n" +
                "FROM films f\n" +
                "LEFT JOIN mpa_ratings mr ON mr.id = f.mpa_rating_id\n" +
                "LEFT JOIN likes l on f.id = l.film_id\n" +
                "GROUP BY f.id\n" +
                "ORDER BY COUNT(l.id) DESC\n" +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> {
            try {
                return mapRowToFilm(resultSet, rowNum);
            } catch (ExistenceException e) {
                throw new RuntimeException(e);
            }
        }, count);
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        String sqlQuery = "SELECT f.id AS film_id, f.name AS film_name, f.description AS film_description, " +
                "f.release_date, f.duration, f.mpa_rating_id, \n" +
                "mpa.name AS mpa_name, mpa.description AS mpa_description, \n" +
                "COUNT(l.film_id) AS likes_count\n" +
                "FROM films f\n" +
                "INNER JOIN likes l ON f.id = l.film_id\n" +
                "INNER JOIN mpa_ratings mpa ON f.mpa_rating_id = mpa.id\n" +
                "WHERE l.user_id IN (?, ?)\n" +
                "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id, mpa.name, mpa.description\n" +
                "HAVING COUNT(DISTINCT l.user_id) = ?\n" +
                "ORDER BY likes_count DESC";
        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> {
            try {
                return mapRowToFilm(resultSet, rowNum);
            } catch (ExistenceException e) {
                throw new RuntimeException(e);
            }
        }, userId, friendId, friendId);
    }

    @Override
    public Film getFilmById(int filmId) throws ExistenceException {
        String sqlQuery = "SELECT f.id,  f.name, f.description, f.release_date, f.duration, f.mpa_rating_id, " +
                "mr.name AS mpa_name, mr.description AS mpa_description\n" +
                "FROM films f\n" +
                "LEFT JOIN mpa_ratings mr ON mr.id = f.mpa_rating_id\n" +
                "WHERE f.id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, (resultSet, rowNum) -> {
                try {
                    return mapRowToFilm(resultSet, rowNum);
                } catch (ExistenceException e) {
                    throw new RuntimeException(e);
                }
            }, filmId);
        } catch (EmptyResultDataAccessException e) {
            String errorMessage = "Фильма с таким идентификатором нет в списке";
            throw new ExistenceException(errorMessage);
        }
    }

    @Override
    public void removeFilmById(int id) {
        String sqlQuery = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<Film> getFilmsDirectorSorted(int directorid, String sortby) throws ExistenceException {

        String sqlOrderBy = "";
        if (sortby.equals("likes"))
            sqlOrderBy = "COUNT(l.id) DESC";
        else
            sqlOrderBy = "release_date ";
        String sqlQuery = "SELECT f.id,  f.name, f.description, f.release_date, f.duration, f.mpa_rating_id, " +
                "mr.name AS mpa_name, mr.description AS mpa_description\n" +
                "FROM films f\n" +
                "INNER JOIN directors_films df ON df.film_id = f.id\n" +
                "LEFT JOIN mpa_ratings mr ON mr.id = f.mpa_rating_id\n" +
                "LEFT JOIN likes l on f.id = l.film_id\n" +
                "WHERE df.director_id = ? \n" +
                "GROUP BY f.id\n" +
                "ORDER BY " + sqlOrderBy + ";";
        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> {
            try {
                return mapRowToFilm(resultSet, rowNum);
            } catch (ExistenceException e) {
                throw new RuntimeException(e);
            }
        }, directorid);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException, ExistenceException {
        return Film.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(MpaRating.builder().id(resultSet.getInt("mpa_rating_id"))
                        .name(resultSet.getString("MPA_NAME"))
                        .description(resultSet.getString("mpa_description"))
                        .build())
                .build();
    }
}
