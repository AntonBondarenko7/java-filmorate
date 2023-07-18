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
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@Rollback
public class LikeStorageTests {
    private final LikeStorage likeStorage;
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final MpaRatingStorage mpaStorage;
    private int userId;
    private int filmId;

    @BeforeEach
    void setUp() throws ExistenceException, ValidationException {
        User user = User.builder()
                .email("test@example.com")
                .login("testuser")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Film film = Film.builder()
                .name("Test film")
                .description("Test description")
                .releaseDate(LocalDate.parse("1990-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .duration(120)
                .mpa(mpaStorage.getMpaRatingById(1))
                .build();

        User createdUser = userDbStorage.createUser(user);
        userId = createdUser.getId();

        Film createdFilm = filmDbStorage.createFilm(film);
        filmId = createdFilm.getId();
    }

    @Test
    public void testCreateLike() {
        likeStorage.createLike(userId, filmId);

        List<Like> likes = likeStorage.getLikesByUserId(userId);
        assertEquals(1, likes.size());
        Like like = likes.get(0);
        assertThat(like.getUserId()).isEqualTo(userId);
        assertThat(like.getFilmId()).isEqualTo(filmId);
    }

    @Test
    public void testDeleteLike() {
        likeStorage.createLike(userId, filmId);
        likeStorage.deleteLike(userId, filmId);

        List<Like> likes = likeStorage.getLikesByUserId(userId);
        assertTrue(likes.isEmpty());
    }

    @Test
    public void testDeleteAllLikes() {
        likeStorage.createLike(userId, filmId);
        likeStorage.deleteAllLikes();

        List<Like> likes = likeStorage.getLikesByUserId(userId);
        assertTrue(likes.isEmpty());
    }

    @Test
    public void testGetLikesByUserId() {
        likeStorage.createLike(userId, filmId);

        List<Like> likes = likeStorage.getLikesByUserId(userId);

        assertEquals(1, likes.size());
        assertEquals(filmId, likes.get(0).getFilmId());
        assertEquals(userId, likes.get(0).getUserId());
    }

    @Test
    public void testGetLikesByFilmId() {
        likeStorage.createLike(userId, filmId);

        List<Like> likes = likeStorage.getLikesByFilmId(filmId);

        assertEquals(1, likes.size());
        assertEquals(filmId, likes.get(0).getFilmId());
        assertEquals(userId, likes.get(0).getUserId());
    }
}
