package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film newFilm);

    Collection<Film> getAll();

    Film getFilmById(long id);

    void delete(long id);

    Film addLike(long filmId, long id);

    Film deleteLike(long filmId, long id);

    Collection<Film.Mpa> getAllMpa();

    Film.Mpa getMpaById(long id);

    Collection<Film.Genre> getAllGenre();

    Film.Genre getGenreById(long id);
}
