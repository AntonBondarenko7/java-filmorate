package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GenreStorage {

    Optional<Genre> getById(int id);

    List<Genre> getAll();

    List<Genre> getFilmGenre(long filmId);

    Genre createGenre(Genre genre);

    void setFilmGenre(long filmId, int genreId);

    boolean deleteGenresOfFilm(long id);

    void loadGenres(Collection<Film> films);

    void updateGenresForFilm(Film film);
}
