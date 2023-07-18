package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@Primary
@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaRatingStorage mpaStorage;
    private int id = 0;
    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaRatingStorage mpaStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = mpaStorage;
    }

    @Override
    public Film createFilm(Film film) throws ValidationException {
        try {
            FilmValidator.validateFilm(film);
            int filmId = generateId();
            film.setId(filmId);
            String sqlQuery = "INSERT INTO \"films\"(\"id\", \"name\", \"description\", \"release_date\", " +
                    "\"duration\", \"mpa_rating_id\") VALUES (?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sqlQuery,
                    filmId,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId());
            return film;
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException, ExistenceException {
        getFilmById(film.getId());
        try {
            FilmValidator.validateFilm(film);
            String sqlQuery = "UPDATE \"films\" SET " +
                    "\"name\" = ?, \"description\" = ?, \"release_date\" = ?, \"duration\" = ?, \"mpa_rating_id\" = ?" +
                    "WHERE \"id\" = ?";
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
        String sqlQuery = "SELECT * FROM \"films\"";
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

    public List<Film> getMostPopularFilms(int count) throws ExistenceException {
        String sqlQuery = "SELECT * " +
                "FROM \"films\"\n" +
                "LEFT JOIN \"likes\" on \"films\".\"id\" = \"likes\".\"film_id\"\n" +
                "GROUP BY \"films\".\"id\"\n" +
                "ORDER BY COUNT(\"likes\".\"id\") DESC\n" +
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
    public Film getFilmById(int filmId) throws ExistenceException {
        String sqlQuery = "SELECT * from \"films\" WHERE \"id\" = ?";
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

    private int getCurrentMaxId() {
        String sqlQuery = "SELECT max(\"id\") FROM  \"films\"";
        if (jdbcTemplate.queryForObject(sqlQuery, Integer.class) == null) {
            return 0;
        } else {
            return jdbcTemplate.queryForObject(sqlQuery, Integer.class);
        }
    }

    public boolean deleteFilmById(int id) {
        String sqlQuery = "DELETE FROM \"films\"" +
                "WHERE \"id\" = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException, ExistenceException {
        return Film.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(mpaStorage.getMpaRatingById(resultSet.getInt("mpa_rating_id")))
                .build();
    }
}
