package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Collection;
import java.util.HashMap;

@RestController
@RequestMapping("/films")
public class FilmController {
    private int id = 0;
    private final HashMap<Integer, Film> films = new HashMap<>();
    FilmValidator validator = new FilmValidator();

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@RequestBody Film film) throws ValidationException {
        validator.validateFilm(film);
        Integer filmId = generateId();
        film.setId(filmId);
        films.put(filmId, film);
        return new ResponseEntity<>(film, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) throws ValidationException {
        if (films.containsKey(film.getId())) {
            validator.validateFilm(film);
            films.put(film.getId(), film);
            return new ResponseEntity<>(film, HttpStatus.OK);
        } else {
            throw new ValidationException("Фильма с таким идентификатором нет в списке");
        }
    }

    private int generateId() {
        id++;
        return id;
    }
}
