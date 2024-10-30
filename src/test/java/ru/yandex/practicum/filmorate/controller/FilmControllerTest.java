package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {

    private FilmController filmController;
    private Film film;

    @BeforeEach
    void setUp() {
        film = new Film();
        film.setName("Valid Name");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
    }

    @Test
    void shouldThrowExceptionForEmptyName() {
        film.setName("");

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> filmController.create(film));

        assertEquals("название не может быть пустым", thrown.getMessage());
    }

    @Test
    void shouldThrowExceptionForTooLongDescription() {
        film.setDescription("a".repeat(201));

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> filmController.create(film));

        assertEquals("максимальная длина описания — 200 символов", thrown.getMessage());
    }

    @Test
    void shouldThrowExceptionForReleaseDateBefore1895() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> filmController.create(film));

        assertEquals("дата релиза — не раньше 28 декабря 1895 года", thrown.getMessage());
    }

    @Test
    void shouldThrowExceptionForNonPositiveDuration() {
        film.setDuration(0);

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> filmController.create(film));

        assertEquals("продолжительность фильма должна быть положительным числом", thrown.getMessage());
    }

    @Test
    void shouldThrowExceptionForEmptyRequest() {
        Film emptyFilm = new Film();

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> filmController.create(emptyFilm));

        assertEquals("название не может быть пустым", thrown.getMessage());
    }

    @Test
    void shouldCreateValidFilm() {
        assertDoesNotThrow(() -> filmController.create(film));
    }
}
