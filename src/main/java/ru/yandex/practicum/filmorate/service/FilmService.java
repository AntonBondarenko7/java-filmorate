package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

    public void addLike(int filmId, int userId) throws ValidationException, ExistenceException {
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

    public void removeLike(int filmId, int userId) throws ValidationException, ExistenceException {
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

    public Collection<Film> getMostPopularFilms(int count, int genreId, int year) throws ExistenceException {
        Collection<Film> films = filmStorage.getMostPopularFilms(count, genreId, year);
        filmDirectorStorage.loadDirectors(films);
        filmGenreStorage.loadGenres(films);
        reviewStorage.loadReviews(films);
        return films;
    }


    public Collection<Film> getFilmsDirectorSorted(int directorId, String sortBy) throws ExistenceException {
        Collection<Film> films = filmStorage.getFilmsDirectorSorted(directorId, sortBy);
        if (films.isEmpty())
            throw new ExistenceException("Фильмы не найдены");
        filmDirectorStorage.loadDirectors(films);
        filmGenreStorage.loadGenres(films);
        reviewStorage.loadReviews(films);
        return films;
    }

    public Collection<Film> getAllFilms() throws ExistenceException {
        Collection<Film> films = filmStorage.getAllFilms().values();
        filmDirectorStorage.loadDirectors(films);
        filmGenreStorage.loadGenres(films);
        reviewStorage.loadReviews(films);
        return films;
    }

    public Film getFilmById(int filmId) throws ValidationException, ExistenceException {
        Film film = filmStorage.getFilmById(filmId);
        film.setDirectors(filmDirectorStorage.getFilmDirectorsByFilmId(filmId));
        Collection<Film> films = List.of(film);
        filmGenreStorage.loadGenres(films);
        reviewStorage.loadReviews(films);
        return film;
    }

    public Film createFilm(Film film) throws ValidationException, ExistenceException {
        try {
            FilmValidator.validateFilm(film);
            Film createdFilm = filmStorage.createFilm(film);

            if (film.getDirectors() != null) {
                for (Director d : film.getDirectors()) {
                    filmDirectorStorage.createFilmDirector(createdFilm.getId(), d.getId());
                }
            }
            createdFilm.setDirectors(filmDirectorStorage.getFilmDirectorsByFilmId(createdFilm.getId()));
            return setFilmGenres(createdFilm);
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    private Film setFilmGenres(Film film) {
        if (film.getGenres() != null) {
            filmGenreStorage.updateGenresForFilm(film);

            film.getGenres().clear();
            Collection<Film> films = List.of(film);
            filmGenreStorage.loadGenres(films);
        }
        return film;
    }

    public Film updateFilm(Film film) throws ValidationException, ExistenceException {
        try {
            filmDirectorStorage.deleteAllFilmDirectorsByFilmId(film.getId());
            Film updatedFilm = filmStorage.updateFilm(film);

            if (film.getDirectors() != null) {
                for (Director d : film.getDirectors()) {
                    filmDirectorStorage.createFilmDirector(film.getId(), d.getId());
                }
            }
            updatedFilm.setDirectors(filmDirectorStorage.getFilmDirectorsByFilmId(updatedFilm.getId()));
            return setFilmGenres(updatedFilm);
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    public void removeFilmById(int id) throws ValidationException {
        if (id <= 0) {
            throw new ValidationException("id должен быть положительным");
        }
        filmStorage.removeFilmById(id);
    }

    public List<Film> getCommonFilms(int userId, int friendId) throws ExistenceException {
        List<Film> films = filmStorage.getCommonFilms(userId, friendId);
        filmDirectorStorage.loadDirectors(films);
        filmGenreStorage.loadGenres(films);
        reviewStorage.loadReviews(films);
        return films;
    }

    public Collection<Film> getRecommendations(int userId) throws ExistenceException {
        Collection<Film> films = filmStorage.getRecommendations(userId);
        filmDirectorStorage.loadDirectors(films);
        filmGenreStorage.loadGenres(films);
        reviewStorage.loadReviews(films);
        return films;
    }

    public List<Film> searchFilms(String query, String searchType) throws ValidationException, ExistenceException {
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
