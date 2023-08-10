package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public List<Genre> getAll() {
        log.info("Получен GET запрос");
        return genreService.findAllGenres();
    }

    @GetMapping("/{id}")
    public Genre get(@PathVariable int id) {
        log.info("Получен GET запрос");
        return genreService.getById(id);
    }

    @GetMapping("/film/{id}")
    public List<Genre> findGenresByFilmId(@PathVariable int id) {
        return genreService.getFilmGenres(id);
    }

    @PostMapping
    public Genre addGenre(@RequestBody Genre genre) {
        return genreService.createGenre(genre);
    }
}
