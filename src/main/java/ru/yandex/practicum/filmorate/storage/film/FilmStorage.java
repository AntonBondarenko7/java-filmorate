package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;

public interface FilmStorage {
    public Film createFilm(Film film) throws ValidationException;
    public Film updateFilm(Film film) throws ValidationException;
    public Collection<Film> getAllFilmsValues();
    public int generateId();
    public HashMap<Integer, Film> getAllFilms();
    public int getFilmLikesCount(Film film);
    public TreeSet<Film> getMostPopularFilms();
}
