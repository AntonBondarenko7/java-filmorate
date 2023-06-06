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
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFilmById(@PathVariable int id) throws ExistenceException, ValidationException {
        return new ResponseEntity<>(filmService.getFilmById(id), HttpStatus.OK);
    }

    @GetMapping("/popular")
    public ResponseEntity<?> getMostPopularFilms(@RequestParam(required = false) Integer count) {
        return new ResponseEntity<>(filmService.getMostPopularFilms(Objects.requireNonNullElse(count, 10)),
                HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createFilm(@RequestBody Film film) throws ValidationException {
        filmService.createFilm(film);
        return new ResponseEntity<>(film, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<?> updateFilm(@RequestBody Film film) throws ValidationException, ExistenceException {
        filmService.updateFilm(film);
        return new ResponseEntity<>(film, HttpStatus.OK);
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
}
