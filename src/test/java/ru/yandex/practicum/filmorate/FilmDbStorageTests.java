package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTests {
    private final FilmDbStorage filmDbStorage;
    private final MpaRatingStorage mpaStorage;

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
}
