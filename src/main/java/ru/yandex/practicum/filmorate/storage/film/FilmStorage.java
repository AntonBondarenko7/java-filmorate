package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;

public interface FilmStorage {
    Film createFilm(Film film) throws ValidationException;
    Film updateFilm(Film film) throws ValidationException, ExistenceException;
    Collection<Film> getAllFilmsValues();
    int generateId();
    HashMap<Integer, Film> getAllFilms();
    int getFilmLikesCount(Film film);
    TreeSet<Film> getMostPopularFilms();
    Film getFilmById(int filmId) throws ValidationException, ExistenceException;
}
