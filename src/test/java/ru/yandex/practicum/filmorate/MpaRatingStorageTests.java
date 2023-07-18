package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaRatingStorageTests {
    private final MpaRatingStorage mpaRatingStorage;

    @BeforeEach
    void seUp(){
        mpaRatingStorage.deleteAllMpaRatings();
    }

    @Test
    public void testCreateMpaStorage() {
        String name = "Test";
        String description = "Test description";

        mpaRatingStorage.createMpaRating(name, description);
        List<MpaRating> mpa = mpaRatingStorage.getAllMpaRatings();

        assertFalse(mpa.isEmpty());
        assertEquals(name, mpa.get(0).getName());
        assertEquals(description, mpa.get(0).getDescription());
    }


    @Test
    public void testDeleteMpaRating() {
        String name = "Test";
        String description = "Test description";

        mpaRatingStorage.createMpaRating(name, description);
        List<MpaRating> mpa = mpaRatingStorage.getAllMpaRatings();
        mpaRatingStorage.deleteMpaRating(mpa.get(0).getId());
        mpa = mpaRatingStorage.getAllMpaRatings();

        assertTrue(mpa.isEmpty());
    }

    @Test
    public void testDeleteAllMpaRatings() {
        String name = "Test";
        String description = "Test description";

        mpaRatingStorage.createMpaRating(name, description);
        mpaRatingStorage.deleteAllMpaRatings();
        List<MpaRating> mpa = mpaRatingStorage.getAllMpaRatings();

        assertTrue(mpa.isEmpty());
    }

    @Test
    public void testGetAllMpaRatings() {
        String name = "Test";
        String description = "Test description";

        mpaRatingStorage.createMpaRating(name, description);
        mpaRatingStorage.createMpaRating(name + " 1", description + " 1");
        List<MpaRating> mpa = mpaRatingStorage.getAllMpaRatings();

        assertEquals(2, mpa.size());
    }

    @Test
    public void testGetMpaRatingById() throws ExistenceException {
        String name = "Test";
        String description = "Test description";
        mpaRatingStorage.createMpaRating(name, description);
        List<MpaRating> mpa = mpaRatingStorage.getAllMpaRatings();

        MpaRating retrievedMpa = mpaRatingStorage.getMpaRatingById(mpa.get(0).getId());


        assertEquals(name, retrievedMpa.getName());
        assertEquals(description, retrievedMpa.getDescription());
    }

    @Test
    public void testUpdateMpaRating() throws ExistenceException {
        String name = "Test";
        String description = "Test description";
        mpaRatingStorage.createMpaRating(name, description);
        List<MpaRating> mpa = mpaRatingStorage.getAllMpaRatings();
        int mpaId = mpa.get(0).getId();

        String updatedName = "Updated test";
        String updatedDescription = "Updated Test description";

        MpaRating mpaRatingToUpdate = MpaRating.builder()
                                            .id(mpaId)
                                            .name(updatedName)
                                            .description(updatedDescription).build();

        MpaRating updatedMpaRating = mpaRatingStorage.updateMpaRating(mpaRatingToUpdate);

        assertEquals(mpaRatingToUpdate.getName(), updatedMpaRating.getName());
        assertEquals(mpaRatingToUpdate.getDescription(), updatedMpaRating.getDescription());
    }
}
