package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import java.util.Collection;

@Component
@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController extends AdviceController {
    private final GenreStorage genreStorage;

    @GetMapping
    public Collection<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGenreById(@PathVariable int id) throws ExistenceException {
        return new ResponseEntity<>(genreStorage.getGenreById(id), HttpStatus.OK);
    }
}