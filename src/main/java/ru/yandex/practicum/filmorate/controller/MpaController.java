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
@RequestMapping("/mpa")
@Slf4j
@RequiredArgsConstructor
public class MpaController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film.Mpa> getAllMpa() {
        return filmService.getAllMpa();
    }

    @GetMapping("/{id}")
    public Film.Mpa getMpaById(@PathVariable long id) {
        return filmService.getMpaById(id);
    }
}
