package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User create(User user);

    User update(User newUser);

    Collection<User> getAll();

    User getUserById(long id);

    User addFriend(long id, long friendId);

    Collection<User> getUserFriends(long id);

    User deleteFriend(long id, long friendId);
}
