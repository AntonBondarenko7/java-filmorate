package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@Rollback
public class FriendshipStorageTests {
    private final UserDbStorage userDbStorage;
    private final FriendshipStorage friendshipStorage;
    private int userId;
    private int friendId;

    @BeforeEach
    void setUp() throws ValidationException {
        User user = User.builder()
                .email("test@example.com")
                .login("testuser")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User friend = User.builder()
                .email("friend@example.com")
                .login("friend")
                .name("friend")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User createdUser = userDbStorage.createUser(user);
        User createdFriend = userDbStorage.createUser(friend);
        userId = createdUser.getId();
        friendId = createdFriend.getId();
    }

    @Test
    public void testCreateFriendship() {
        friendshipStorage.createFriendship(userId, friendId);

        List<Friendship> userFriendships = friendshipStorage.getUserFriendships(userId);
        assertEquals(1, userFriendships.size());
        Friendship friendship = userFriendships.get(0);
        assertThat(friendship.getUser1Id()).isEqualTo(userId);
        assertThat(friendship.getUser2Id()).isEqualTo(friendId);
        assertThat(friendship.isApproved()).isFalse();
    }

    @Test
    public void testDeleteFriendship() {
        friendshipStorage.createFriendship(userId, friendId);
        friendshipStorage.deleteFriendship(userId, friendId);

        List<Friendship> userFriendships = friendshipStorage.getUserFriendships(userId);
        assertTrue(userFriendships.isEmpty());
    }

    @Test
    public void testGetUserFriendships() {
        friendshipStorage.createFriendship(userId, friendId);
        List<Friendship> userFriendships = friendshipStorage.getUserFriendships(userId);

        assertEquals(1, userFriendships.size());
        assertEquals(friendId, userFriendships.get(0).getUser2Id());
    }

    @Test
    public void testUpdateFriendship() {
        friendshipStorage.createFriendship(userId, friendId);
        friendshipStorage.updateFriendship();
        List<Friendship> userFriendships = friendshipStorage.getUserFriendships(userId);

        assertEquals(1, userFriendships.size());
        assertThat(userFriendships.get(0).isApproved()).isTrue();
    }

    @Test
    public void testGetCommonFriendIds() throws ValidationException {
        User commonFriend = User.builder()
                .email("commonFriend@example.com")
                .login("commonFriend")
                .name("commonFriend")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User createdCommonFriend = userDbStorage.createUser(commonFriend);
        friendshipStorage.createFriendship(userId, friendId);
        friendshipStorage.createFriendship(userId, createdCommonFriend.getId());
        friendshipStorage.createFriendship(friendId, createdCommonFriend.getId());

        List<Integer> commonFriendIds = friendshipStorage.getCommonFriendIds(userId, friendId);
        assertEquals(1, commonFriendIds.size());
        assertTrue(commonFriendIds.contains(createdCommonFriend.getId()));
    }
}
