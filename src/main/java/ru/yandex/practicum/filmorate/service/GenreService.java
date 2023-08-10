package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;

    public List<Genre> findAllGenres() {
        return genreStorage.getAll();
    }

    public Genre getById(int id) {
        checkId(id);
        return genreStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Жанра с id " + id + " нет в базе"));
    }

    private void checkId(long userId) {
        if (userId <= 0) {
            throw new NotFoundException("id должен быть положительным");
        }
    }

    public List<Genre> getFilmGenres(int filmId) {
        checkId(filmId);
        return genreStorage.getFilmGenre(filmId);
    }

    public Genre createGenre(Genre genre) {
        return genreStorage.createGenre(genre);
    }
}