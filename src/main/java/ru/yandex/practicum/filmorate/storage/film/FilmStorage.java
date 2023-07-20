package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

public interface FilmStorage {
    Film createFilm(Film film) throws ValidationException;

    Film updateFilm(Film film) throws ValidationException, ExistenceException;

    default int generateId() {
        throw new UnsupportedOperationException("Метод не поддерживается");
    }

    HashMap<Integer, Film> getAllFilms();

    default int getFilmLikesCount(Film film) {
        throw new UnsupportedOperationException("Метод не поддерживается");
    }

    Film getFilmById(int filmId) throws ValidationException, ExistenceException;

    default List<Film> getMostPopularFilms(int count) throws ExistenceException {
        throw new UnsupportedOperationException("Метод не поддерживается");
    }

    default TreeSet<Film> getMostPopularFilms() {
        throw new UnsupportedOperationException("Метод не поддерживается");
    }
}
