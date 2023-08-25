package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmGenreDBStorage;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@Rollback
public class GenreStorageTests {
    private final FilmGenreDBStorage genreStorage;

    @BeforeEach
    void setUp() {
        genreStorage.deleteAllGenres();
    }

    @Test
    public void testCreateGenre() {
        String genreName = "Test genre";
        Genre genre = new Genre();
        genre.setName(genreName);
        genreStorage.createGenre(genre);

        LinkedList<Genre> genres = new LinkedList<>();
        genres.addAll(genreStorage.getAll());

        assertFalse(genres.isEmpty());
        assertEquals(genreName, genres.getLast().getName());
    }

    @Test
    public void testDeleteGenre() {
        String genreName = "Test genre";
        Genre genre = new Genre();
        genre.setName(genreName);
        genreStorage.createGenre(genre);

        List<Genre> genres = genreStorage.getAll();
        int genreId = genres.get(0).getId();

        genreStorage.deleteGenre(genreId);
        genres = genreStorage.getAll();

        assertTrue(genres.isEmpty());
    }

    @Test
    public void testDeleteAllGenres() {
        genreStorage.createGenre(new Genre(1,"Test Genre"));
        genreStorage.deleteAllGenres();
        List<Genre> genres = genreStorage.getAll();

        assertTrue(genres.isEmpty());
    }

    @Test
    public void testGetAllGenres() {
        genreStorage.createGenre(new Genre(1,"Test Genre"));
        genreStorage.createGenre(new Genre(2,"Test Genre2"));

        List<Genre> genres = genreStorage.getAll();

        assertEquals(2, genres.size());
    }

    @Test
    public void testGetGenreById() {
        String genreName = "Test genre";
        genreStorage.createGenre(new Genre(1, genreName));
        List<Genre> genres = genreStorage.getAll();
        int genreId = genres.get(0).getId();

        Genre genre = genreStorage.getById(genreId).get();

        assertThat(genre.getName()).isEqualTo(genreName);
    }

    @Test
    public void testUpdateGenre() {
        String updatedName = "Test genre updated";
        genreStorage.createGenre(new Genre(1,"Test genre updated"));
        List<Genre> genres = genreStorage.getAll();
        int genreId = genres.get(0).getId();

        Genre genreToUpdate = Genre.builder().id(genreId).name(updatedName).build();

        genreStorage.updateGenre(genreToUpdate);

        Genre retrievedGenre = genreStorage.getById(genreId).get();
        assertThat(retrievedGenre.getName()).isEqualTo(updatedName);
    }
}
