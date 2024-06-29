package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    static {
        ((ch.qos.logback.classic.Logger) log).setLevel(ch.qos.logback.classic.Level.WARN);
    }

    private final Map<Long, User> usersMap = new HashMap<>();

    @PostMapping
    public User create(@RequestBody User user) {
        log.trace("Начало создания экземпляра {}", user);
        validate(user);

        user.setId(getNextId());
        usersMap.put(user.getId(), user);
        log.trace("Экземпляр {} создан", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.trace("Начало изменения экземпляра {}", newUser);
        validate(newUser);

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
        throw new ValidationException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    @GetMapping
    public Collection<User> getAll() {
        log.trace("Получение записей коллекции");
        return usersMap.values();
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

    private long getNextId() {
        long currentMaxId = usersMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
