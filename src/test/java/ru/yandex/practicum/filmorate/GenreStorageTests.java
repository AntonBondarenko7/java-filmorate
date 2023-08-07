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
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@Rollback
public class GenreStorageTests {
    private final GenreStorage genreStorage;

    @BeforeEach
    void setUp() {
        genreStorage.deleteAllGenres();
    }

    @Test
    public void testCreateGenre() {
        String genreName = "Test Genre";
        genreStorage.createGenre(genreName);

        List<Genre> genres = genreStorage.getAllGenres();

        assertFalse(genres.isEmpty());
        assertEquals(genreName, genres.get(0).getName());
    }

    @Test
    public void testDeleteGenre() {
        genreStorage.createGenre("Test Genre");
        List<Genre> genres = genreStorage.getAllGenres();
        int genreId = genres.get(0).getId();

        genreStorage.deleteGenre(genreId);
        genres = genreStorage.getAllGenres();

        assertTrue(genres.isEmpty());
    }

    @Test
    public void testDeleteAllGenres() {
        genreStorage.createGenre("Test Genre");
        genreStorage.deleteAllGenres();
        List<Genre> genres = genreStorage.getAllGenres();

        assertTrue(genres.isEmpty());
    }

    @Test
    public void testGetAllGenres() {
        genreStorage.createGenre("Test Genre");
        genreStorage.createGenre("Test Genre2");

        List<Genre> genres = genreStorage.getAllGenres();

        assertEquals(2, genres.size());
    }

    @Test
    public void testGetGenreById() throws ExistenceException {
        String genreName = "Test genre";
        genreStorage.createGenre(genreName);
        List<Genre> genres = genreStorage.getAllGenres();
        int genreId = genres.get(0).getId();

        Genre genre = genreStorage.getGenreById(genreId);

        assertThat(genre.getName()).isEqualTo(genreName);
    }

    @Test
    public void testUpdateGenre() throws ExistenceException {
        String updatedName = "Test genre updated";
        genreStorage.createGenre("Test genre");
        List<Genre> genres = genreStorage.getAllGenres();
        int genreId = genres.get(0).getId();

        Genre genreToUpdate = Genre.builder().id(genreId).name(updatedName).build();

        genreStorage.updateGenre(genreToUpdate);

        Genre retrievedGenre = genreStorage.getGenreById(genreId);
        assertThat(retrievedGenre.getName()).isEqualTo(updatedName);
        assertThat(retrievedGenre.getName()).isEqualTo(updatedName);
    }
}
