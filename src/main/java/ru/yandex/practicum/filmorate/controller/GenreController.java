package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@Slf4j
@RequiredArgsConstructor
public class GenreController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film.Genre> getAllGenre() {
        return filmService.getAllGenre();
    }

    @GetMapping("/{id}")
    public Film.Genre getGenreById(@PathVariable long id) {
        return filmService.getGenreById(id);
    }
}
