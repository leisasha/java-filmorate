package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

    static {
        ((ch.qos.logback.classic.Logger) log).setLevel(ch.qos.logback.classic.Level.WARN);
    }

    private final Map<Long, Film> filmsMap = new HashMap<>();

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.trace("Начало создания экземпляра {}", film);
        validate(film);

        film.setId(getNextId());
        filmsMap.put(film.getId(), film);
        log.trace("Экземпляр {} создан", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.trace("Начало изменения экземпляра {}", newFilm);
        validate(newFilm);

        if (filmsMap.containsKey(newFilm.getId())) {
            Film oldFilm = filmsMap.get(newFilm.getId());
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            log.trace("Экземпляр {} изменен", newFilm);
            return oldFilm;
        }
        log.warn("Фильм с id = " + newFilm.getId() + " не найден");
        throw new ValidationException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    @GetMapping
    public Collection<Film> getAll() {
        log.trace("Получение записей коллекции");
        return filmsMap.values();
    }

    private void validate(Film film) throws ValidationException {
        log.trace("Начало валидации {}", film);

        if (Optional.ofNullable(film.getName()).isEmpty() || film.getName().isBlank()) {
            log.warn("Валидация не пройдена: название фильма пустое или отсутствует");
            throw new ValidationException("название не может быть пустым");
        }
        log.trace("Валидация названия пройдена");

        if (Optional.ofNullable(film.getDescription()).isPresent() && !film.getDescription().isBlank()
                && film.getDescription().length() > 200) {
            log.warn("Валидация не пройдена: длина описания фильма превышает 200 символов");
            throw new ValidationException("максимальная длина описания — 200 символов");
        }
        log.trace("Валидация описания пройдена");

        if (Optional.ofNullable(film.getReleaseDate()).isPresent()
                && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Валидация не пройдена: дата релиза раньше 28 декабря 1895 года");
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }
        log.trace("Валидация даты релиза пройдена");

        if (film.getDuration() <= 0) {
            log.warn("Валидация не пройдена: продолжительность фильма должна быть положительным числом");
            throw new ValidationException("продолжительность фильма должна быть положительным числом");
        }
        log.trace("Валидация продолжительности пройдена");

        log.trace("Валидация фильма {} успешно завершена", film);
    }

    private long getNextId() {
        long currentMaxId = filmsMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
