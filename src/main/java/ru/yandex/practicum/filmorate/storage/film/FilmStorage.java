package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmSortBy;

import java.util.HashMap;
import java.util.List;

public interface FilmStorage {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    HashMap<Integer, Film> getAllFilms();

    Film getFilmById(int filmId);

    List<Film> getMostPopularFilms(int count, int genreId, int year);

    List<Film> getCommonFilms(int userId, int friendId);

    List<Film> getFilmsDirectorSorted(int directorId, FilmSortBy sortBy);

    List<Film> getRecommendations(int userId);

    void removeFilmById(int id);

    List<Film> searchFilms(String query, String searchType);
}
