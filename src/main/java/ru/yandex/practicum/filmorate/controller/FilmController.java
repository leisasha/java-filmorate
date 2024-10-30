package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        return filmService.update(newFilm);
    }

    @GetMapping
    public Collection<Film> getAll() {
        return filmService.getAll();
    }

    @PutMapping("/{film_id}/like/{id}")
    public Film addLike(@PathVariable long film_id, @PathVariable long id) {
        return filmService.addLike(film_id, id);
    }

    @DeleteMapping("/{film_id}/like/{id}")
    public Film deleteLike(@PathVariable long film_id, @PathVariable long id) {
        return filmService.deleteLike(film_id, id);
    }

    @GetMapping("/popular")
    public Collection<Film> getFilmPopular(@RequestParam(required = false) Long count) {
        return filmService.getFilmPopular(count);
    }
}
