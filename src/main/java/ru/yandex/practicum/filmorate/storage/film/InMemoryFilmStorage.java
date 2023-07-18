package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.FilmComparator;
import ru.yandex.practicum.filmorate.validator.FilmValidator;
import java.util.HashMap;
import java.util.TreeSet;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private int id = 0;
    private final HashMap<Integer, Film> films = new HashMap<>();
    private final TreeSet<Film> mostPopularFilms = new TreeSet<>(new FilmComparator());

    @Override
    public Film createFilm(Film film) throws ValidationException {
        try {
            FilmValidator.validateFilm(film);
            int filmId = generateId();
            film.setId(filmId);
            films.put(filmId, film);
            mostPopularFilms.add(film);
            return film;
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException, ExistenceException {
        if (films.containsKey(film.getId())) {
            try {
                FilmValidator.validateFilm(film);
                mostPopularFilms.removeIf(film1 -> film1.getId() == id);
                films.put(film.getId(), film);
                mostPopularFilms.add(film);
                return film;
            } catch (ValidationException e) {
                throw new ValidationException(e.getMessage());
            }
        } else {
            String errorMessage = "Фильма с таким идентификатором нет в списке";
            throw new ExistenceException(errorMessage);
        }
    }

    @Override
    public HashMap<Integer, Film> getAllFilms() {
        return films;
    }

    public TreeSet<Film> getMostPopularFilms() {
        return mostPopularFilms;
    }

    @Override
    public int generateId() {
        id++;
        return id;
    }

    @Override
    public int getFilmLikesCount(Film film) {
        return film.getLikes().toArray().length;
    }

    @Override
    public Film getFilmById(int filmId) throws ExistenceException {
        if (films.containsKey(filmId)) {
            return films.get(filmId);
        } else {
            String errorMessage = "Фильма с таким идентификатором нет в списке";
            throw new ExistenceException(errorMessage);
        }
    }
}
