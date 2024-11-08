package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> usersMap = new HashMap<>();

    public User create(User user) {
        user.setId(getNextId());
        usersMap.put(user.getId(), user);
        log.trace("Экземпляр {} создан", user);
        return user;
    }

    public User update(User newUser) {
        if (usersMap.containsKey(newUser.getId())) {
            User oldUser = usersMap.get(newUser.getId());
            oldUser.setName(newUser.getName());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setEmail(newUser.getEmail());
            oldUser.setBirthday(newUser.getBirthday());
            log.trace("Экземпляр {} изменен", newUser);
            return oldUser;
        }
        log.warn("Пользователь с id = " + newUser.getId() + " не найден");
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    public Collection<User> getAll() {
        return usersMap.values();
    }

    public User getUserById(long id) {
        if (usersMap.containsKey(id))
            return usersMap.get(id);
        else
            throw new NotFoundException("Пользователя с " + id + " не существует.");
    }

    private long getNextId() {
        long currentMaxId = usersMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
