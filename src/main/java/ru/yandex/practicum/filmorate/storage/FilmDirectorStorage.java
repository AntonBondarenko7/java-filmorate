package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FilmDirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    public boolean deleteAllFilmDirectorsByFilmId(int filmId) {
        String sqlQuery = "DELETE FROM directors_films WHERE film_id = ?";
        return jdbcTemplate.update(sqlQuery, filmId) > 0;
    }

    public void updateDirectorsForFilm(Film film) {
        StringBuilder sb = new StringBuilder();
        if (film.getDirectors().isEmpty()) {
            sb.append("DELETE FROM directors_films WHERE film_id = " + film.getId() + " ");
        } else {
            List<Integer> filmDirectors = film.getDirectors().stream().map(Director::getId).distinct().collect(Collectors.toList());

            sb.append("DELETE FROM directors_films WHERE film_id = " + film.getId() + " ; " + "INSERT INTO directors_films (film_id, director_id) VALUES ");

            for (Integer genreId : filmDirectors) {
                sb.append("( " + film.getId() + ", " + genreId + " ), ");
            }
            int length = sb.length();
            sb.deleteCharAt(length - 2);
        }
        String sql = sb.toString();
        jdbcTemplate.update(sql);
    }

    public void loadDirectors(Collection<Film> films) {
        final Map<Integer, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        final String sqlQuery = "SELECT d.* , film_id FROM directors_films df join directors d on d.director_id = df.director_id  WHERE film_id  in (" + inSql + ")" + "ORDER BY d.director_id ASC";

        jdbcTemplate.query(sqlQuery, (ResultSet rs) -> {
            final Film film = filmById.get(rs.getInt("film_id"));
            film.addDirector(mapRowToDirector(rs));
        }, films.stream().map(Film::getId).toArray());
    }

    private Director mapRowToDirector(ResultSet resultSet) throws SQLException {
        return Director.builder().id(resultSet.getInt("director_id")).name(resultSet.getString("name")).build();
    }
}
