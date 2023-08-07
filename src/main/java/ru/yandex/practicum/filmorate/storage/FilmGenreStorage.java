package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class FilmGenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmGenreStorage(JdbcTemplate jdbcTemplate,
                            GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
    }

    public void createFilmGenre(int filmId, int genreId) throws ExistenceException {
        genreStorage.getGenreById(genreId);
        String sqlQuery = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, genreId);
    }

    public boolean deleteAllFilmGenresByFilmId(int filmId) {
        String sqlQuery = "DELETE FROM film_genres WHERE film_id = ?";
        return jdbcTemplate.update(sqlQuery, filmId) > 0;
    }

    public Set<Genre> getFilmGenresByFilmId(int filmId) throws ExistenceException {
        String sqlQuery = "SELECT * FROM film_genres WHERE film_id = ? ORDER BY genre_id ASC";
        List<FilmGenre> fglist = jdbcTemplate.query(sqlQuery, this::mapRowToFilmGenre, filmId);
        Set<Genre> gSet = new HashSet<>();
        for (FilmGenre fg : fglist) {
            gSet.add(genreStorage.getGenreById(fg.getGenreId()));
        }
        return gSet;
    }

    private FilmGenre mapRowToFilmGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return FilmGenre.builder()
                .id(resultSet.getInt("id"))
                .filmId(resultSet.getInt("film_id"))
                .genreId(resultSet.getInt("genre_id"))
                .build();
    }
}
