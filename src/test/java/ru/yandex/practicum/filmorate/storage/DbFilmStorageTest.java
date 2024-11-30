package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.DbFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.DbUserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({DbFilmStorage.class, DbUserStorage.class})
public class DbFilmStorageTest {
    private final DbFilmStorage filmStorage;
    private final DbUserStorage userStorage;
    private Film film;
    private User user;

    @BeforeEach
    void setUp() {
        film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2024, 1, 1));
        film.setDuration(120);
        film.setMpa(new Film.Mpa(1, "G"));

        user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("leisasha");
        user.setName("Eren");
        user.setBirthday(LocalDate.of(2024, 1, 1));
    }

    @Test
    public void testCreateFilm() {
        Film createdFilm = filmStorage.create(film);

        assertThat(createdFilm).isNotNull();
        assertThat(createdFilm.getId()).isNotNull();
        assertThat(createdFilm.getName()).isEqualTo(film.getName());
        assertThat(createdFilm.getDescription()).isEqualTo(film.getDescription());
    }

    @Test
    public void testUpdateFilm() {
        Film createdFilm = filmStorage.create(film);
        createdFilm.setName("Updated Film");
        createdFilm.setDescription("Updated Description");
        Film updatedFilm = filmStorage.update(createdFilm);

        assertThat(updatedFilm).isNotNull();
        assertThat(updatedFilm.getName()).isEqualTo("Updated Film");
        assertThat(updatedFilm.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    public void testDeleteFilm() {
        Film createdFilm = filmStorage.create(film);

        filmStorage.delete(createdFilm.getId());

        assertThatThrownBy(() -> filmStorage.getFilmById(createdFilm.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void testGetAllFilms() {
        Film createdFilm = filmStorage.create(film);
        Collection<Film> films = filmStorage.getAll();

        assertThat(films).isNotNull();
        assertThat(films).isNotEmpty();
    }

    @Test
    public void testGetFilmById() {
        Film createdFilm = filmStorage.create(film);
        Film film = filmStorage.getFilmById(createdFilm.getId());

        assertThat(film).isNotNull();
        assertThat(film.getId()).isEqualTo(createdFilm.getId());
    }

    @Test
    public void testAddLike() {
        Film createdFilm = filmStorage.create(film);
        User createdUser = userStorage.create(user);

        Film filmWithLike = filmStorage.addLike(createdFilm.getId(), createdUser.getId());

        assertThat(filmWithLike.getLikes()).contains(createdUser.getId());
    }

    @Test
    public void testDeleteLike() {
        Film createdFilm = filmStorage.create(film);
        User createdUser = userStorage.create(user);

        filmStorage.addLike(createdFilm.getId(), createdUser.getId());
        Film filmWithoutLike = filmStorage.deleteLike(createdFilm.getId(), createdUser.getId());

        assertThat(filmWithoutLike.getLikes()).doesNotContain(createdUser.getId());
    }

    @Test
    public void testGetAllMpa() {
        Collection<Film.Mpa> mpaList = filmStorage.getAllMpa();

        assertThat(mpaList).isNotNull();
        assertThat(mpaList).isNotEmpty();
    }

    @Test
    public void testGetMpaById() {
        int mpaId = 1;
        Film.Mpa mpa = filmStorage.getMpaById(mpaId);

        assertThat(mpa).isNotNull();
        assertThat(mpa.getId()).isEqualTo(mpaId);
    }

    @Test
    public void testGetAllGenres() {
        Collection<Film.Genre> genres = filmStorage.getAllGenre();

        assertThat(genres).isNotNull();
        assertThat(genres).isNotEmpty();
    }

    @Test
    public void testGetGenreById() {
        int genreId = 1;
        Film.Genre genre = filmStorage.getGenreById(genreId);

        assertThat(genre).isNotNull();
        assertThat(genre.getId()).isEqualTo(genreId);
    }
}
