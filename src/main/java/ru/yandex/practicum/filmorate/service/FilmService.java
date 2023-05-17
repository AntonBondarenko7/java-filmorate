package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addLike(Long userId, Film film) throws ValidationException {
        film.getLikes().add(userId);
        filmStorage.updateFilm(film);
        return film;
    }

    public Film removeLike(Long userId, Film film) throws ValidationException {
        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);
        return film;
    }

    public ArrayList<Film> getMostPopularFilms(int count) {
        ArrayList<Film> mostPopularFilms = new ArrayList<>();
        filmStorage.getMostPopularFilms().stream().limit(count).forEach(mostPopularFilms::add);
        return mostPopularFilms;
    }
}
