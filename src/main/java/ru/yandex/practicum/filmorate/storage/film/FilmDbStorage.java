package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film createFilm(Film film) {
        try {
            String sqlQuery = "INSERT INTO films (name, description, release_date, " +
                    "duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sqlQuery,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId());

            film.setId(getFilmByNameAndReleaseDate(film.getName(), film.getReleaseDate(), film.getDuration()).getId());
            return film;
        } catch (ValidationException | ExistenceException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    @Override
    public Film updateFilm(Film film) {
        getFilmById(film.getId());
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

    public Film getFilmByNameAndReleaseDate(String name, LocalDate releaseDate, int duration) {
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
    public List<Film> getMostPopularFilms(int count, int genreId, int year) {
        String sqlQuery =
                "SELECT f.id,  f.name, f.description, f.release_date, f.duration, f.mpa_rating_id, " +
                        "mr.name AS mpa_name, mr.description AS mpa_description\n" +
                        "FROM films f\n" +
                        "LEFT JOIN mpa_ratings mr ON mr.id = f.mpa_rating_id\n" +
                        "LEFT JOIN likes l on f.id = l.film_id\n" +
                        "LEFT JOIN film_genres as FG on f.id = FG.FILM_ID\n" +
                        "WHERE (genre_id = ? OR ? = 0) AND (EXTRACT(YEAR FROM RELEASE_DATE) = ? OR ? = 0)\n" +
                        "GROUP BY f.id\n" +
                        "ORDER BY COUNT(l.id) DESC\n" +
                        "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> {
            try {
                return mapRowToFilm(resultSet, rowNum);
            } catch (ExistenceException e) {
                throw new RuntimeException(e);
            }
        }, genreId, genreId, year, year, count);
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
    public Film getFilmById(int filmId) {
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
    public List<Film> getFilmsDirectorSorted(int directorId, String sortBy) {
        String sqlOrderBy = "";
        if (sortBy.equals("likes"))
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
        }, directorId);
    }

    @Override

    public List<Film> getRecommendations(int userId) {
        String sqlQuery = "SELECT DISTINCT f.id,  f.name, f.description, f.release_date, f.duration, f.mpa_rating_id, " +
                "mr.name AS mpa_name, mr.description AS mpa_description\n" +
                "FROM FILMS F\n" +
                " LEFT JOIN mpa_ratings mr ON mr.id = f.mpa_rating_id\n" +
                " JOIN LIKES L ON L.film_id = F.id\n" +
                " JOIN (Select L1.user_id as userMaxSamples\n" + //юзеры с максимальным пересечением по лайкам. L2 - наш юзер, L1 - остальные
                "        FROM LIKES L1 \n" +
                "        JOIN LIKES L2 ON L1.film_id = L2.film_id AND L1.user_id <> L2.user_id AND L2.user_id = ?\n" +
                "        GROUP BY L1.user_id\n" +
                "        ORDER BY COUNT(*) DESC) as MS ON MS.userMaxSamples = L.user_id\n" +
                " LEFT JOIN LIKES L3 ON L3.film_id = L.film_id AND L3.user_id = ?\n" +
                "WHERE L3.film_id IS NULL \n" + //находим только те фильмы, которые наш юзер не лайкал.
                "LIMIT 10;";
        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> {
            try {
                return mapRowToFilm(resultSet, rowNum);
            } catch (ExistenceException e) {
                throw new RuntimeException(e);
            }
        }, userId, userId);
    }


    public List<Film> searchFilms(String query, String searchType) {
        query = "%" + query + "%";
        String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, f.duration,\n" +
                "f.mpa_rating_id, mpa.name AS mpa_name, mpa.description AS mpa_description,\n" +
                "COUNT(l.film_id) AS likes_count\n" +
                "FROM films f\n" +
                "LEFT JOIN mpa_ratings mpa on f.mpa_rating_id = mpa.id\n" +
                "LEFT JOIN likes l on f.id = l.film_id\n" +
                "LEFT JOIN directors_films df on f.id = df.film_id\n" +
                "LEFT JOIN directors d on df.director_id = d.director_id\n";
        if (searchType.equals("title")) {
            sqlQuery += "WHERE LOWER(f.name) like LOWER(?)\n" +
                    "GROUP BY f.id, f.name, f.description, f.release_date, " +
                    "f.duration, f.mpa_rating_id, mpa.name, mpa.description\n" +
                    "ORDER BY likes_count DESC";
            return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> {
                try {
                    return mapRowToFilm(resultSet, rowNum);
                } catch (ExistenceException e) {
                    throw new RuntimeException(e);
                }
            }, query);
        } else if (searchType.equals("director")) {
            sqlQuery += "WHERE LOWER(d.name) like LOWER(?)\n" +
                    "GROUP BY f.id, f.name, f.description, f.release_date, " +
                    "f.duration, f.mpa_rating_id, mpa.name, mpa.description\n" +
                    "ORDER BY likes_count DESC";
            return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> {
                try {
                    return mapRowToFilm(resultSet, rowNum);
                } catch (ExistenceException e) {
                    throw new RuntimeException(e);
                }
            }, query);
        } else {
            sqlQuery += "WHERE LOWER(f.name) like LOWER(?)\n" +
                    "OR LOWER(d.name) like LOWER(?)\n" +
                    "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id, mpa.name, mpa.description\n" +
                    "ORDER BY likes_count DESC";
            return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> {
                try {
                    return mapRowToFilm(resultSet, rowNum);
                } catch (ExistenceException e) {
                    throw new RuntimeException(e);
                }
            }, query, query);
        }
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
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
                .directors(new HashSet<>())
                .build();
    }
}
