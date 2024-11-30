package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.DbUserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(DbUserStorage.class)
public class DbUserStorageTest {

    private final DbUserStorage userStorage;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testUser");
        user.setName("Test Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    public void testCreateUser() {
        User createdUser = userStorage.create(user);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(createdUser.getLogin()).isEqualTo(user.getLogin());
    }

    @Test
    public void testCreateUserWithExistingEmail() {
        userStorage.create(user);
        user.setLogin("anotherLogin");

        assertThatThrownBy(() -> userStorage.create(user))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Email уже занят");
    }

    @Test
    public void testUpdateUser() {
        User createdUser = userStorage.create(user);
        createdUser.setName("Updated Name");

        User updatedUser = userStorage.update(createdUser);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
    }

    @Test
    public void testUpdateNonExistentUser() {
        user.setId(999L);
        assertThatThrownBy(() -> userStorage.update(user))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    public void testGetAllUsers() {
        userStorage.create(user);

        Collection<User> users = userStorage.getAll();

        assertThat(users).isNotEmpty();
        assertThat(users).extracting(User::getEmail).contains(user.getEmail());
    }

    @Test
    public void testGetUserById() {
        User createdUser = userStorage.create(user);
        User foundUser = userStorage.getUserById(createdUser.getId());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(createdUser.getId());
    }

    @Test
    public void testGetUserByNonExistentId() {
        assertThatThrownBy(() -> userStorage.getUserById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    public void testAddFriend() {
        User user1 = userStorage.create(user);
        User user2 = new User();
        user2.setEmail("friend@mail.ru");
        user2.setLogin("friendUser");
        user2.setName("Friend Name");
        user2.setBirthday(LocalDate.of(2000, 2, 2));
        User friend = userStorage.create(user2);

        User updatedUser = userStorage.addFriend(user1.getId(), friend.getId());

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getFriends()).contains(friend.getId());
    }

    @Test
    public void testGetUserFriends() {
        User user1 = userStorage.create(user);
        User user2 = new User();
        user2.setEmail("friend@mail.ru");
        user2.setLogin("friendUser");
        user2.setName("Friend Name");
        user2.setBirthday(LocalDate.of(2000, 2, 2));
        User friend = userStorage.create(user2);

        userStorage.addFriend(user1.getId(), friend.getId());
        Collection<User> friends = userStorage.getUserFriends(user1.getId());

        assertThat(friends).isNotEmpty();
        assertThat(friends).extracting(User::getId).contains(friend.getId());
    }

    @Test
    public void testDeleteFriend() {
        User user1 = userStorage.create(user);
        User user2 = new User();
        user2.setEmail("friend@mail.ru");
        user2.setLogin("friendUser");
        user2.setName("Friend Name");
        user2.setBirthday(LocalDate.of(2000, 2, 2));
        User friend = userStorage.create(user2);

        userStorage.addFriend(user1.getId(), friend.getId());
        User updatedUser = userStorage.deleteFriend(user1.getId(), friend.getId());

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getFriends()).doesNotContain(friend.getId());
    }
}

