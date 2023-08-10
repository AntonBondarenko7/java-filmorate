package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.Collection;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController extends AdviceController {
    private final FilmService filmService;

    @GetMapping()
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getMostPopularFilms(
            @RequestParam(value = "count", defaultValue = "10") @Positive Integer count,
            @RequestParam(value = "genreId", defaultValue = "0") @Positive Integer genreId,
            @RequestParam(value = "year", defaultValue = "0") @Min(1895) Integer year
    ) {
        return filmService.getMostPopularFilms(count, genreId, year);
    }


    @GetMapping("/director/{directorId}")
    public Collection<Film> getFilmsDirectorSorted(@PathVariable Integer directorId, @RequestParam String sortBy) {
        return filmService.getFilmsDirectorSorted(directorId, sortBy);
    }

    @GetMapping("/common")
    public Collection<Film> getCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/search")
    public Collection<Film> searchFilms(@RequestParam String query, @RequestParam String by) {
        return filmService.searchFilms(query, by);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping()
    public Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        filmService.removeLike(id, userId);
    }

    @DeleteMapping("/{id}")
    public void removeFilmById(@PathVariable int id) {
        filmService.removeFilmById(id);
    }
}
