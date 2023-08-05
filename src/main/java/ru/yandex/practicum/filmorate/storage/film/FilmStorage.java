package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

public interface FilmStorage {
    Film createFilm(Film film) throws ValidationException;

    Film updateFilm(Film film) throws ValidationException, ExistenceException;

    HashMap<Integer, Film> getAllFilms();

    Film getFilmById(int filmId) throws ValidationException, ExistenceException;

    List<Film> getMostPopularFilms(int count, int genreId, int year) throws ExistenceException;

    List<Film> getCommonFilms(int userId, int friendId);

    List<Film> getFilmsDirectorSorted(int directorid, String sortby) throws ExistenceException;

    void removeFilmById(int id);

    List<Film> searchFilms(String query, String searchType);
}
