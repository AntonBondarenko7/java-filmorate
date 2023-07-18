package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@Rollback
public class FilmDbStorageTests {
    private final FilmDbStorage filmDbStorage;
    private final MpaRatingStorage mpaStorage;
    private final UserDbStorage userDbStorage;
    private final LikeStorage likeStorage;

    @BeforeEach
    void setUp() throws ExistenceException, ValidationException {
        Film film = Film.builder()
                .name("Test film")
                .description("Test description")
                .releaseDate(LocalDate.parse("1990-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .duration(120)
                .mpa(mpaStorage.getMpaRatingById(1))
                .build();

        filmDbStorage.createFilm(film);
    }

    @Test
    public void testCreateFilm() throws ValidationException, ExistenceException {
        Film film = Film.builder()
                .name("Test film")
                .description("Test description")
                .releaseDate(LocalDate.parse("1990-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .duration(120)
                .mpa(mpaStorage.getMpaRatingById(1))
                .build();

        Film createdFilm = filmDbStorage.createFilm(film);

        assertNotNull(createdFilm);
        assertEquals(film.getName(), createdFilm.getName());
        assertEquals(film.getDescription(), createdFilm.getDescription());
        assertEquals(film.getReleaseDate(), createdFilm.getReleaseDate());
        assertEquals(film.getDuration(), createdFilm.getDuration());
        assertEquals(film.getMpa(), createdFilm.getMpa());
    }

    @Test
    public void testUpdateFilm() throws ExistenceException, ValidationException {
        int filmId = filmDbStorage.getAllFilms().keySet().stream().findFirst().get().intValue();
        Film film = Film.builder()
                .id(filmId)
                .name("Test film")
                .description("Test description")
                .releaseDate(LocalDate.parse("1990-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .duration(120)
                .mpa(mpaStorage.getMpaRatingById(1))
                .build();

        Film updatedFilm = filmDbStorage.updateFilm(film);
        assertNotNull(updatedFilm);
        assertEquals(film.getName(), updatedFilm.getName());
        assertEquals(film.getDescription(), updatedFilm.getDescription());
        assertEquals(film.getReleaseDate(), updatedFilm.getReleaseDate());
        assertEquals(film.getDuration(), updatedFilm.getDuration());
        assertEquals(film.getMpa(), updatedFilm.getMpa());
    }

    @Test
    public void testGetAllFilms() {
        HashMap<Integer, Film> filmsMap = filmDbStorage.getAllFilms();

        assertNotNull(filmsMap);
        assertFalse(filmsMap.isEmpty());
    }

    @Test
    public void testGetMostPopularFilms() throws ValidationException, ExistenceException {
        User user = User.builder()
                .email("test@example.com")
                .login("testuser")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User createdUser = userDbStorage.createUser(user);

        Film film = Film.builder()
                .name("Test film")
                .description("Test description")
                .releaseDate(LocalDate.parse("1990-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .duration(120)
                .mpa(mpaStorage.getMpaRatingById(1))
                .build();

        Film createdFilm = filmDbStorage.createFilm(film);
        likeStorage.createLike(createdUser.getId(), createdFilm.getId());
        List<Film> popFilms = filmDbStorage.getMostPopularFilms(10);

        assertEquals(createdFilm.getId(), popFilms.get(0).getId());
    }

    @Test
    public void testGetFilmById() throws ExistenceException, ValidationException {
        Film film = Film.builder()
                .name("Test film2")
                .description("Test description2")
                .releaseDate(LocalDate.parse("1990-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .duration(120)
                .mpa(mpaStorage.getMpaRatingById(1))
                .build();

        Film createdFilm = filmDbStorage.createFilm(film);
        Film retrievedFilm = filmDbStorage.getFilmById(createdFilm.getId());

        assertNotNull(retrievedFilm);
        assertEquals(createdFilm.getName(), retrievedFilm.getName());
        assertEquals(createdFilm.getDescription(), retrievedFilm.getDescription());
        assertEquals(createdFilm.getReleaseDate(), retrievedFilm.getReleaseDate());
        assertEquals(createdFilm.getDuration(), retrievedFilm.getDuration());
        assertEquals(createdFilm.getMpa(), retrievedFilm.getMpa());
    }

    @Test
    public void testDeleteFilmById() {
        int filmId = filmDbStorage.getAllFilms().keySet().stream().findFirst().get().intValue();
        filmDbStorage.deleteFilmById(filmId);
        assertThrows(ExistenceException.class, () -> filmDbStorage.getFilmById(filmId));
    }
}
