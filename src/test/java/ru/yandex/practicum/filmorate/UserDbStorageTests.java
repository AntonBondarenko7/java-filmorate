package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTests {
    private final UserDbStorage userStorage;

    @BeforeEach
    void setUp() throws ValidationException {
        User user = User.builder()
                .email("test@example.com")
                .login("testuser")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        userStorage.createUser(user);
    }

    @AfterEach
    void tearDown() {
        userStorage.deleteAllUsers();
    }

    @Test
    public void testCreateUser() throws ValidationException {
        User user = User.builder()
                .email("test@example.com")
                .login("testuser")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();


        User createdUser = userStorage.createUser(user);

        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertEquals(user.getName(), createdUser.getName());
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertEquals(user.getLogin(), createdUser.getLogin());
        assertEquals(user.getBirthday(), createdUser.getBirthday());
    }

    @Test
    public void testUpdateUser() throws ValidationException, ExistenceException {
        int userId = userStorage.getAllUsers().keySet().stream().findFirst().get().intValue();
        User userToUpdate = User.builder()
                .id(userId)
                .email("updated@example.com")
                .login("updateduser")
                .name("Updated User")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();

        User updatedUser = userStorage.updateUser(userToUpdate);

        assertNotNull(updatedUser);
        assertEquals(userToUpdate.getEmail(), updatedUser.getEmail());
        assertEquals(userToUpdate.getLogin(), updatedUser.getLogin());
        assertEquals(userToUpdate.getName(), updatedUser.getName());
        assertEquals(userToUpdate.getBirthday(), updatedUser.getBirthday());
    }

    @Test
    public void testGetAllUsers() {
        HashMap<Integer, User> userMap = userStorage.getAllUsers();

        assertNotNull(userMap);
        assertFalse(userMap.isEmpty());
    }

    @Test
    public void testGetUserById() throws ExistenceException, ValidationException {
        User user = User.builder()
                .email("test2@example.com")
                .login("testuser2")
                .name("Test User2")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User createdUser = userStorage.createUser(user);
        User retrievedUser = userStorage.getUserById(createdUser.getId());

        assertNotNull(retrievedUser);
        assertEquals(createdUser.getId(), retrievedUser.getId());
        assertEquals(retrievedUser.getName(), createdUser.getName());
        assertEquals(retrievedUser.getEmail(), createdUser.getEmail());
        assertEquals(retrievedUser.getLogin(), createdUser.getLogin());
        assertEquals(retrievedUser.getBirthday(), createdUser.getBirthday());
    }

    @Test
    public void testDeleteUserById() {
        int userId = userStorage.getAllUsers().keySet().stream().findFirst().get().intValue();
        userStorage.deleteUserById(userId);
        assertThrows(ExistenceException.class, () -> {userStorage.getUserById(userId);});
    }

    @Test
    public void testDeleteAllUsers() {
        userStorage.deleteAllUsers();
        assertTrue(userStorage.getAllUsers().isEmpty());
    }
}
