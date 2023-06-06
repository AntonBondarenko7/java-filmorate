package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public void addLike(int filmId, int userId) throws ValidationException, ExistenceException {
        if (userStorage.getUserById(userId) == null) {
            throw new ExistenceException("Пользователя с таким идентификатором не существует");
        } else {
            Film film = filmStorage.getFilmById(filmId);
            film.getLikes().add(userId);
            filmStorage.updateFilm(film);
        }
    }

    public void removeLike(int filmId, int userId) throws ValidationException, ExistenceException {
        if (userStorage.getUserById(userId) == null) {
            throw new ExistenceException("Пользователя с таким идентификатором не существует");
        } else {
            Film film = filmStorage.getFilmById(filmId);
            film.getLikes().remove(userId);
            filmStorage.updateFilm(film);
        }
    }

    public ArrayList<Film> getMostPopularFilms(int count) {
        ArrayList<Film> mostPopularFilms = new ArrayList<>();
        filmStorage.getMostPopularFilms().stream().limit(count).forEach(mostPopularFilms::add);
        return mostPopularFilms;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms().values();
    }

    public Film getFilmById(int filmId) throws ValidationException, ExistenceException {
        return filmStorage.getFilmById(filmId);
    }

    public Film createFilm(Film film) throws ValidationException {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) throws ValidationException, ExistenceException {
        return filmStorage.updateFilm(film);
    }
}
