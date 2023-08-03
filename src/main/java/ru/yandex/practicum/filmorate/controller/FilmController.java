package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.Objects;

@Component
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController extends AdviceController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getAllFilms() throws ExistenceException {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFilmById(@PathVariable int id) throws ExistenceException, ValidationException {
        return new ResponseEntity<>(filmService.getFilmById(id), HttpStatus.OK);
    }

    @GetMapping("/popular")
    public ResponseEntity<?> getMostPopularFilms(@RequestParam(required = false) Integer count) throws ExistenceException {
        return new ResponseEntity<>(filmService.getMostPopularFilms(Objects.requireNonNullElse(count, 10)),
                HttpStatus.OK);
    }

    @GetMapping("/director/{directorId}")
    public ResponseEntity<?> getFilmsDirectorSorted(@PathVariable Integer directorId, @RequestParam String sortBy) throws ExistenceException {
        return new ResponseEntity<>(filmService.getFilmsDirectorSorted(directorId, sortBy),
                HttpStatus.OK);
    }

    @GetMapping("/common")
    public ResponseEntity<?> getCommonFilms(@RequestParam int userId, @RequestParam int friendId) throws ExistenceException {
        return new ResponseEntity<>(filmService.getCommonFilms(userId, friendId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createFilm(@RequestBody Film film) throws ValidationException, ExistenceException {
        return new ResponseEntity<>(filmService.createFilm(film), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<?> updateFilm(@RequestBody Film film) throws ValidationException, ExistenceException {
        return new ResponseEntity<>(filmService.updateFilm(film), HttpStatus.OK);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<?> addLike(@PathVariable int id,
                                     @PathVariable int userId) throws ValidationException, ExistenceException {
        filmService.addLike(id, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<?> removeLike(@PathVariable int id,
                                        @PathVariable int userId) throws ValidationException, ExistenceException {
        filmService.removeLike(id, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeFilmById(@PathVariable int id) throws ValidationException {
        filmService.removeFilmById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
