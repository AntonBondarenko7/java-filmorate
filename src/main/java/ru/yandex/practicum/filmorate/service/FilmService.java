package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;
    private final FilmGenreStorage filmGenreStorage;

    public void addLike(int filmId, int userId) throws ValidationException, ExistenceException {
        userStorage.getUserById(userId);
        filmStorage.getFilmById(filmId);
        likeStorage.createLike(userId, filmId);
    }

    public void removeLike(int filmId, int userId) throws ValidationException, ExistenceException {
        userStorage.getUserById(userId);
        filmStorage.getFilmById(filmId);
        likeStorage.deleteLike(userId, filmId);
    }

    public Collection<Film> getMostPopularFilms(int count) throws ExistenceException {
        Collection<Film> films = filmStorage.getMostPopularFilms(count);
        for (Film f: films) {
            f.setGenres(filmGenreStorage.getFilmGenresByFilmId(f.getId()));
        }
        return films;
    }

    public Collection<Film> getAllFilms() throws ExistenceException {
        Collection<Film> films = filmStorage.getAllFilms().values();
        for (Film f: films) {
            f.setGenres(filmGenreStorage.getFilmGenresByFilmId(f.getId()));
        }
        return films;
    }

    public Film getFilmById(int filmId) throws ValidationException, ExistenceException {
        Film film = filmStorage.getFilmById(filmId);
        film.setGenres(filmGenreStorage.getFilmGenresByFilmId(filmId));
        return film;
    }

    public Film createFilm(Film film) throws ValidationException, ExistenceException {
        Film createdFilm = filmStorage.createFilm(film);
        if (film.getGenres() != null) {
            for (Genre g : film.getGenres()) {
                filmGenreStorage.createFilmGenre(film.getId(), g.getId());
            }
        }
        createdFilm.setGenres(filmGenreStorage.getFilmGenresByFilmId(createdFilm.getId()));
        return createdFilm;
    }

    public Film updateFilm(Film film) throws ValidationException, ExistenceException {
        filmGenreStorage.deleteAllFilmGenresByFilmId(film.getId());
        if (film.getGenres() != null) {
            for (Genre g : film.getGenres()) {
                filmGenreStorage.createFilmGenre(film.getId(), g.getId());
            }
        }
        Film updatedFilm = filmStorage.updateFilm(film);
        updatedFilm.setGenres(filmGenreStorage.getFilmGenresByFilmId(updatedFilm.getId()));
        return updatedFilm;
    }
}
