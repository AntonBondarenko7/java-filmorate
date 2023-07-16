package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
        String sqlQuery = "INSERT INTO \"film_genres\" (\"film_id\", \"genre_id\")" +
                          "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, genreId);
    }

    public boolean deleteFilmGenre(int filmId, int genreId) {
        String sqlQuery = "DELETE FROM \"film_genres\" " +
                          "WHERE \"film_id\" = ? AND \"genre_id\" = ?";
        return jdbcTemplate.update(sqlQuery,filmId, genreId) > 0;
    }

    public boolean deleteAllFilmGenresByFilmId(int filmId) {
        String sqlQuery = "DELETE FROM \"film_genres\" " +
                "WHERE \"film_id\" = ?";
        return jdbcTemplate.update(sqlQuery,filmId) > 0;
    }

    public List<Genre> getFilmGenresByFilmId(int filmId) throws ExistenceException {
        String sqlQuery = "SELECT * FROM \"film_genres\" WHERE \"film_id\" = ?";
        List<FilmGenre> fglist = jdbcTemplate.query(sqlQuery, this::mapRowToFilmGenre, filmId);
        List<Genre> glist = new ArrayList<>();
        for (FilmGenre fg : fglist) {
            glist.add(genreStorage.getGenreById(fg.getGenreId()));
        }
        return glist;
    }

    private FilmGenre mapRowToFilmGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return FilmGenre.builder()
                .id(resultSet.getInt("id"))
                .filmId(resultSet.getInt("film_id"))
                .genreId(resultSet.getInt("genre_id"))
                .build();
    }
}
