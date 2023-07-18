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
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmGenreStorageTests {
    private final FilmDbStorage filmDbStorage;
    private final MpaRatingStorage mpaStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final int genreId = 1;
    private int filmId;

    @BeforeEach
    void setUp() throws ExistenceException, ValidationException {
        Film film = Film.builder()
                .name("Test film")
                .description("Test description")
                .releaseDate(LocalDate.parse("1990-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .duration(120)
                .mpa(mpaStorage.getMpaRatingById(1))
                .build();

        Film createdFilm = filmDbStorage.createFilm(film);
        filmId = createdFilm.getId();
    }

    @Test
    public void testCreateFilmGenre() throws ExistenceException {
        filmGenreStorage.createFilmGenre(filmId, genreId);
        Set<Genre> filmGenres = filmGenreStorage.getFilmGenresByFilmId(filmId);
        assertThat(filmGenres.size()).isEqualTo(1);
        Genre genre = filmGenres.iterator().next();
        assertThat(genre.getId()).isEqualTo(genreId);
    }

    @Test
    public void testDeleteAllFilmGenresByFilmId() throws ExistenceException {
        filmGenreStorage.createFilmGenre(filmId, genreId);
        filmGenreStorage.deleteAllFilmGenresByFilmId(filmId);
        Set<Genre> filmGenres = filmGenreStorage.getFilmGenresByFilmId(filmId);
        assertThat(filmGenres.isEmpty()).isEqualTo(true);
    }

    @Test
    public void testGetFilmGenresByFilmId() throws ExistenceException {
        filmGenreStorage.createFilmGenre(filmId, genreId);
        Set<Genre> filmGenres = filmGenreStorage.getFilmGenresByFilmId(filmId);

        assertThat(filmGenres.size()).isEqualTo(1);
        assertThat(filmGenres.stream().findFirst().get().getId()).isEqualTo(genreId);
    }
}
