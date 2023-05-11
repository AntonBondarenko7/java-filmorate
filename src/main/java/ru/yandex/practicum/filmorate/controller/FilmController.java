package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Collection;
import java.util.HashMap;

@Component
@RestController
@RequestMapping("/films")
public class FilmController {
    private final InMemoryFilmStorage filmStorage;
    private static final  Logger log = LoggerFactory.getLogger(FilmController.class);

    @Autowired
    public FilmController(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilmsValues();
    }

    @PostMapping
    public ResponseEntity<?> createFilm(@RequestBody Film film) throws ValidationException {
        try {
            filmStorage.createFilm(film);
            return new ResponseEntity<>(film, HttpStatus.CREATED);
        } catch (ValidationException e) {
            log.error(e.getMessage(), e);
            throw new ValidationException(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> updateFilm(@RequestBody Film film) throws ValidationException {
        if (filmStorage.getAllFilms().containsKey(film.getId())) {
            try {
                filmStorage.updateFilm(film);
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

}
