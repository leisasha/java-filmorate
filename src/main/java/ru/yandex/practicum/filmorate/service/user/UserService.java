package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public User create(User user) {
        log.trace("Начало создания экземпляра {}", user);
        validate(user);
        return userStorage.create(user);
    }

    public User update(User newUser) {
        log.trace("Начало изменения экземпляра {}", newUser);
        validate(newUser);
        return userStorage.update(newUser);
    }

    public Collection<User> getAll() {
        log.trace("Получение записей коллекции");
        return userStorage.getAll();
    }

    public User getUserById(long id) {
        log.trace("Получение пользователя по id");
        return userStorage.getUserById(id);
    }

    public User addFriend(long id, long friendId) {
        log.trace("Добавление пользователя в список друзей");
        return userStorage.addFriend(id, friendId);
    }

    public Collection<User> getUserFriends(long id) {
        log.trace("Получение списока друзей");
        return userStorage.getUserFriends(id);
    }

    public User deleteFriend(long id, long friendId) {
        log.trace("Удаление пользователя из списока друзей");
        return userStorage.deleteFriend(id, friendId);
    }

    public Collection<User> getCommonFriend(long id, long friendId) {
        log.trace("Получение списка общих друзей");

        Collection<Long> userFriends = userStorage.getUserById(id).getFriends();
        Collection<Long> friendFriends = userStorage.getUserById(friendId).getFriends();

        Collection<Long> commonFriends = userFriends.stream()
                .filter(friendFriends::contains)
                .collect(Collectors.toSet());

        return commonFriends.stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    private void validate(User user) throws ValidationException {
        log.trace("Начало валидации {}", user);

        if (Optional.ofNullable(user.getEmail()).isEmpty() || user.getEmail().isBlank()
                || !user.getEmail().contains("@")) {
            log.warn("Валидация не пройдена: электронная почта пустая или отсутствует символ '@'");
            throw new ValidationException("электронная почта не может быть пустой и должна содержать символ @");
        }
        log.trace("Валидация электронной почты пройдена");

        if (Optional.ofNullable(user.getLogin()).isEmpty() || user.getLogin().isBlank()
                || user.getLogin().contains(" ")) {
            log.warn("Валидация не пройдена: логин пустой или содержит пробелы");
            throw new ValidationException("логин не может быть пустым и содержать пробелы");
        }
        log.trace("Валидация логина пройдена");

        if (Optional.ofNullable(user.getName()).isEmpty()) {
            log.trace("Имя пользователя пустое, установка логина в качестве имени");
            user.setName(user.getLogin());
        }
        log.trace("Валидация имени пройдена");

        if (Optional.ofNullable(user.getBirthday()).isPresent() && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Валидация не пройдена: дата рождения в будущем");
            throw new ValidationException("дата рождения не может быть в будущем");
        }
        log.trace("Валидация даты рождения пройдена");

        log.trace("Валидация пользователя {} успешно завершена", user);
    }
}
