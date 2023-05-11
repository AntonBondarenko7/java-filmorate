package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Collection;
import java.util.HashMap;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private int id = 0;
    private final HashMap<Integer, Film> films = new HashMap<>();


    @Override
    public Film createFilm(Film film) throws ValidationException {
        try {
            FilmValidator.validateFilm(film);
            Integer filmId = generateId();
            film.setId(filmId);
            films.put(filmId, film);
            return film;
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException {
        if (films.containsKey(film.getId())) {
            try {
                FilmValidator.validateFilm(film);
                films.put(film.getId(), film);
                return film;
            } catch (ValidationException e) {
                throw new ValidationException(e.getMessage());
            }
        } else {
            String errorMessage = "Фильма с таким идентификатором нет в списке";
            throw new ValidationException(errorMessage);
        }
    }

    @Override
    public Collection<Film> getAllFilmsValues() {
        return films.values();
    }

    @Override
    public HashMap<Integer, Film> getAllFilms() {
        return films;
    }

    @Override
    public int generateId() {
        id++;
        return id;
    }
}
