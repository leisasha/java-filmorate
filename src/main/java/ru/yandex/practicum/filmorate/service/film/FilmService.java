package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    public Film create(Film film) {
        log.trace("Начало создания экземпляра {}", film);
        validate(film);
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {
        log.trace("Начало изменения экземпляра {}", newFilm);
        validate(newFilm);
        return filmStorage.update(newFilm);
    }

    public Collection<Film> getAll() {
        log.trace("Получение записей коллекции film");
        return filmStorage.getAll();
    }

    public Film getFilmById(long id) {
        log.trace("Получение film по id");
        return filmStorage.getFilmById(id);
    }

    public Film addLike(long filmId, long id) {
        log.trace("Добавление лайка к фильму");
        return filmStorage.addLike(filmId, id);
    }

    public Film deleteLike(long filmId, long id) {
        log.trace("Удаление лайка из списока");
        return filmStorage.deleteLike(filmId, id);
    }

    public Collection<Film> getFilmPopular(Long count) {
        long countCurent = 10;
        if (count != null && count > 0) {
            countCurent = count;
        }

        Collection<Film> allFilms = filmStorage.getAll();

        return allFilms.stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(countCurent)
                .collect(Collectors.toList());
    }

    public Collection<Film.Mpa> getAllMpa() {
        log.trace("Получение записей коллекции mpa");
        return filmStorage.getAllMpa();
    }

    public Film.Mpa getMpaById(long id) {
        log.trace("Получение записей коллекции mpa по id");
        return filmStorage.getMpaById(id);
    }

    public Collection<Film.Genre> getAllGenre() {
        log.trace("Получение записей коллекции genres");
        return filmStorage.getAllGenre();
    }

    public Film.Genre getGenreById(long id) {
        log.trace("Получение записей коллекции genre по id");
        return filmStorage.getGenreById(id);
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
}
