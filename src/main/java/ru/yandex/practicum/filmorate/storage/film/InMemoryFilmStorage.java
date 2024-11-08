package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> filmsMap = new HashMap<>();

    public Film create(Film film) {
        film.setId(getNextId());
        filmsMap.put(film.getId(), film);
        log.trace("Экземпляр {} создан", film);
        return film;
    }

    public Film update(Film newFilm) {
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
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    public Collection<Film> getAll() {
        return filmsMap.values();
    }

    public Film getFilmById(long id) {
        if (filmsMap.containsKey(id))
            return filmsMap.get(id);
        else
            throw new NotFoundException("Фильма с " + id + " не существует.");
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
