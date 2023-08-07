package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final FilmDirectorStorage filmDirectorStorage;
    private final ReviewStorage reviewStorage;

    public void addLike(int filmId, int userId) throws ValidationException, ExistenceException {
        userStorage.getUserById(userId);
        filmStorage.getFilmById(filmId);
        likeStorage.createLike(userId, filmId);
    }

    public void removeLike(int filmId, int userId) throws ValidationException, ExistenceException {
        userStorage.getUserById(userId);
        filmStorage.getFilmById(filmId);
        likeStorage.deleteLike(userId, filmId);
    }

    public Collection<Film> getMostPopularFilms(int count, int genreId, int year) throws ExistenceException {
        Collection<Film> films = filmStorage.getMostPopularFilms(count, genreId, year);
        for (Film f : films) {
            f.setGenres(filmGenreStorage.getFilmGenresByFilmId(f.getId()));
            f.setDirectors(filmDirectorStorage.getFilmDirectorsByFilmId(f.getId()));
        }
        reviewStorage.loadReviews(films);
        return films;
    }


    public Collection<Film> getFilmsDirectorSorted(int directorId, String sortBy) throws ExistenceException {
        Collection<Film> films = filmStorage.getFilmsDirectorSorted(directorId, sortBy);
        if (films.isEmpty())
            throw new ExistenceException("Фильмы не найдены");
        for (Film f : films) {
            f.setGenres(filmGenreStorage.getFilmGenresByFilmId(f.getId()));
            f.setDirectors(filmDirectorStorage.getFilmDirectorsByFilmId(f.getId()));
        }
        reviewStorage.loadReviews(films);
        return films;
    }

    public Collection<Film> getAllFilms() throws ExistenceException {
        Collection<Film> films = filmStorage.getAllFilms().values();
        for (Film f : films) {
            f.setGenres(filmGenreStorage.getFilmGenresByFilmId(f.getId()));
            f.setDirectors(filmDirectorStorage.getFilmDirectorsByFilmId(f.getId()));
        }
        reviewStorage.loadReviews(films);
        return films;
    }

    public Film getFilmById(int filmId) throws ValidationException, ExistenceException {
        Film film = filmStorage.getFilmById(filmId);
        film.setGenres(filmGenreStorage.getFilmGenresByFilmId(filmId));
        film.setDirectors(filmDirectorStorage.getFilmDirectorsByFilmId(filmId));
        Collection<Film> films = List.of(film);
        reviewStorage.loadReviews(films);
        return film;
    }

    public Film createFilm(Film film) throws ValidationException, ExistenceException {
        Film createdFilm = filmStorage.createFilm(film);
        if (film.getGenres() != null) {
            for (Genre g : film.getGenres()) {
                filmGenreStorage.createFilmGenre(createdFilm.getId(), g.getId());
            }
        }
        createdFilm.setGenres(filmGenreStorage.getFilmGenresByFilmId(createdFilm.getId()));
        if (film.getDirectors() != null) {
            for (Director d : film.getDirectors()) {
                filmDirectorStorage.createFilmDirector(createdFilm.getId(), d.getId());
            }
        }
        createdFilm.setDirectors(filmDirectorStorage.getFilmDirectorsByFilmId(createdFilm.getId()));
        return createdFilm;
    }

    public Film updateFilm(Film film) throws ValidationException, ExistenceException {
        filmGenreStorage.deleteAllFilmGenresByFilmId(film.getId());
        filmDirectorStorage.deleteAllFilmDirectorsByFilmId(film.getId());
        if (film.getGenres() != null) {
            for (Genre g : film.getGenres()) {
                filmGenreStorage.createFilmGenre(film.getId(), g.getId());
            }
        }
        if (film.getDirectors() != null) {
            for (Director d : film.getDirectors()) {
                filmDirectorStorage.createFilmDirector(film.getId(), d.getId());
            }
        }
        Film updatedFilm = filmStorage.updateFilm(film);
        updatedFilm.setGenres(filmGenreStorage.getFilmGenresByFilmId(updatedFilm.getId()));
        updatedFilm.setDirectors(filmDirectorStorage.getFilmDirectorsByFilmId(updatedFilm.getId()));
        return updatedFilm;
    }

    public void removeFilmById(int id) throws ValidationException {
        if (id <= 0) {
            throw new ValidationException("id должен быть положительным");
        }
        filmStorage.removeFilmById(id);
    }

    public List<Film> getCommonFilms(int userId, int friendId) throws ExistenceException {
        List<Film> films = filmStorage.getCommonFilms(userId, friendId);
        for (Film f : films) {
            f.setGenres(filmGenreStorage.getFilmGenresByFilmId(f.getId()));
            f.setDirectors(filmDirectorStorage.getFilmDirectorsByFilmId(f.getId()));
        }
        reviewStorage.loadReviews(films);
        return films;
    }

    public Collection<Film> getRecommendations(int userId) throws ExistenceException {
        Collection<Film> films = filmStorage.getRecommendations(userId);
        for (Film f : films) {
            f.setGenres(filmGenreStorage.getFilmGenresByFilmId(f.getId()));
            f.setDirectors(filmDirectorStorage.getFilmDirectorsByFilmId(f.getId()));
        }
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
        for (Film f : films) {
            f.setGenres(filmGenreStorage.getFilmGenresByFilmId(f.getId()));
            f.setDirectors(filmDirectorStorage.getFilmDirectorsByFilmId(f.getId()));
        }
        return films;
    }


}
