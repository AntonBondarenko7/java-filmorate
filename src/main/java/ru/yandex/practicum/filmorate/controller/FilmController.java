package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Collection;
import java.util.HashMap;


@RestController
@RequestMapping("/films")
public class FilmController {
    private int id = 0;
    private final HashMap<Integer, Film> films = new HashMap<>();
    private static final  Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @PostMapping
    public ResponseEntity<?> createFilm(@RequestBody Film film) throws ValidationException {
        try {
            FilmValidator.validateFilm(film);
            Integer filmId = generateId();
            film.setId(filmId);
            films.put(filmId, film);
            return new ResponseEntity<>(film, HttpStatus.CREATED);
        } catch (ValidationException e) {
            log.error(e.getMessage(), e);
            throw new ValidationException(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> updateFilm(@RequestBody Film film) throws ValidationException {
        if (films.containsKey(film.getId())) {
            try {
                FilmValidator.validateFilm(film);
                films.put(film.getId(), film);
                return new ResponseEntity<>(film, HttpStatus.OK);
            } catch (ValidationException e) {
                log.error(e.getMessage(), e);
                throw new ValidationException(e.getMessage());
            }
        } else {
            String errorMessage = "Фильма с таким идентификатором нет в списке";
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    private int generateId() {
        id++;
        return id;
    }
}
