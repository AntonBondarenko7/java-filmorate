package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmSortBy;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventOperation;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.storage.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmGenreDBStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;
    private final FilmGenreDBStorage filmGenreStorage;
    private final FilmDirectorStorage filmDirectorStorage;
    private final ReviewStorage reviewStorage;
    private final EventService eventService;

    public void addLike(int filmId, int userId) {
        userStorage.getUserById(userId);
        filmStorage.getFilmById(filmId);
        likeStorage.createLike(userId, filmId);
        eventService.addEvent(new Event(
                0,
                System.currentTimeMillis(),
                userId,
                EventType.LIKE,
                EventOperation.ADD,
                filmId));
    }

    public void removeLike(int filmId, int userId) {
        userStorage.getUserById(userId);
        filmStorage.getFilmById(filmId);
        likeStorage.deleteLike(userId, filmId);
        eventService.addEvent(new Event(
                0,
                System.currentTimeMillis(),
                userId,
                EventType.LIKE,
                EventOperation.REMOVE,
                filmId));
    }

    public Collection<Film> getMostPopularFilms(int count, int genreId, int year) {
        Collection<Film> films = filmStorage.getMostPopularFilms(count, genreId, year);
        filmDirectorStorage.loadDirectors(films);
        filmGenreStorage.loadGenres(films);
        reviewStorage.loadReviews(films);
        return films;
    }


    public Collection<Film> getFilmsDirectorSorted(int directorId, FilmSortBy sortBy) {
        Collection<Film> films = filmStorage.getFilmsDirectorSorted(directorId, sortBy);
        if (films.isEmpty())
            throw new ExistenceException("Фильмы не найдены");
        filmDirectorStorage.loadDirectors(films);
        filmGenreStorage.loadGenres(films);
        reviewStorage.loadReviews(films);
        return films;
    }

    public Collection<Film> getAllFilms() {
        Collection<Film> films = filmStorage.getAllFilms().values();
        filmDirectorStorage.loadDirectors(films);
        filmGenreStorage.loadGenres(films);
        reviewStorage.loadReviews(films);
        return films;
    }

    public Film getFilmById(int filmId) {
        Film film = filmStorage.getFilmById(filmId);
        Collection<Film> films = List.of(film);
        filmDirectorStorage.loadDirectors(films);
        filmGenreStorage.loadGenres(films);
        reviewStorage.loadReviews(films);
        return film;
    }

    public Film createFilm(Film film) {
        try {
            FilmValidator.validateFilm(film);
            Film createdFilm = filmStorage.createFilm(film);

            if (film.getDirectors() != null) {
                filmDirectorStorage.updateDirectorsForFilm(film);
            }
            if (film.getGenres() != null)
                filmGenreStorage.updateGenresForFilm(film);
            return setFilmGenres(createdFilm);
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    private Film setFilmGenres(Film film) {
        if (film.getGenres() != null) {
            film.getGenres().clear();
            Collection<Film> films = List.of(film);
            filmGenreStorage.loadGenres(films);
        }
        return film;
    }

    public Film updateFilm(Film film) {
        try {
            filmDirectorStorage.deleteAllFilmDirectorsByFilmId(film.getId());
            Film updatedFilm = filmStorage.updateFilm(film);
            filmGenreStorage.updateGenresForFilm(film);
            if (film.getDirectors() != null)
                filmDirectorStorage.updateDirectorsForFilm(film);
            Collection<Film> films = List.of(updatedFilm);
            filmDirectorStorage.loadDirectors(films);
            return setFilmGenres(updatedFilm);
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    public void removeFilmById(int id) {
        if (id <= 0) {
            throw new ValidationException("id должен быть положительным");
        }
        filmStorage.removeFilmById(id);
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        List<Film> films = filmStorage.getCommonFilms(userId, friendId);
        filmDirectorStorage.loadDirectors(films);
        filmGenreStorage.loadGenres(films);
        reviewStorage.loadReviews(films);
        return films;
    }

    public Collection<Film> getRecommendations(int userId) {
        Collection<Film> films = filmStorage.getRecommendations(userId);
        filmDirectorStorage.loadDirectors(films);
        filmGenreStorage.loadGenres(films);
        reviewStorage.loadReviews(films);
        return films;
    }

    public List<Film> searchFilms(String query, String searchType) {
        if (!searchType.equals("title") &&
                !searchType.equals("director") &&
                !searchType.equals("director,title") &&
                !searchType.equals("title,director")) {
            throw new ValidationException("Некорректные параметры поиска");
        }
        List<Film> films = filmStorage.searchFilms(query, searchType);
        filmDirectorStorage.loadDirectors(films);
        filmGenreStorage.loadGenres(films);
        reviewStorage.loadReviews(films);
        return films;
    }
}
