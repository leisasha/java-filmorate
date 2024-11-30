package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.utility.SqlFileReader;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
@Primary
@RequiredArgsConstructor
public class DbFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    @SneakyThrows
    @Transactional
    @Override
    public Film create(Film film) {
        if (film.getMpa() != null && !doesMpaExist(film.getMpa().getId())) {
            throw new ValidationException("Некорректный идентификатор MPA: " + film.getMpa().getId());
        }
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Film.Genre genre : film.getGenres()) {
                if (!doesGenreExist(genre.getId())) {
                    throw new ValidationException("Некорректный идентификатор жанра: " + genre.getId());
                }
            }
        }

        String sqlQuery = SqlFileReader.readSqlQuery("queries/film/filmsCreate.sql", "createFilm");

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());

            if (film.getMpa() != null) {
                stmt.setInt(5, film.getMpa().getId());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }

            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());

        if (keyHolder.getKey() == null) {
            throw new Throwable("Ошибка при создании фильма, не удалось получить сгенерированный id.");
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String genreQuery = SqlFileReader.readSqlQuery("queries/film/filmsCreate.sql", "genreQuery");

            film.getGenres().stream()
                    .sorted(Comparator.comparing(Film.Genre::getId))
                    .forEach(genre -> jdbcTemplate.update(genreQuery, film.getId(), genre.getId()));
        }

        return film;
    }

    @Transactional
    @Override
    public Film update(Film newFilm) {
        String sqlQuery = SqlFileReader.readSqlQuery("queries/film/filmsUpdate.sql", "updateFilms");

        int resultQuery = jdbcTemplate.update(
                sqlQuery,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getMpa() != null ? newFilm.getMpa().getId() : null,
                newFilm.getId()
        );

        if (resultQuery != 1) {
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        String deleteGenresQuery = SqlFileReader.readSqlQuery("queries/film/filmsUpdate.sql", "deleteGenresQuery");
        jdbcTemplate.update(deleteGenresQuery, newFilm.getId());
        if (newFilm.getGenres() != null && !newFilm.getGenres().isEmpty()) {
            String insertGenresQuery = SqlFileReader.readSqlQuery("queries/film/filmsUpdate.sql", "insertGenresQuery");
            for (Film.Genre genre : newFilm.getGenres()) {
                jdbcTemplate.update(insertGenresQuery, newFilm.getId(), genre.getId());
            }
        }

        return newFilm;
    }

    @Override
    public void delete(long id) {
        String sqlQuery = SqlFileReader.readSqlQuery("queries/film/filmsDelete.sql", "sqlQuery");
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Collection<Film> getAll() {
        String sqlQuery = SqlFileReader.readSqlQuery("queries/film/filmsSelect.sql", "getAllFilms");
        Collection<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm);
        return films;
    }

    @Override
    public Film getFilmById(long id) {
        String sqlQuery = SqlFileReader.readSqlQuery("queries/film/filmsSelect.sql", "getFilmById");

        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm, id);
        if (films.size() != 1) {
            throw new NotFoundException("Фильма с " + id + " не существует.");
        }
        return films.getFirst();
    }

    @Override
    public Film addLike(long filmId, long id) {
        getFilmById(filmId);
        userStorage.getUserById(id);

        String sqlInsertFriendship = SqlFileReader.readSqlQuery("queries/like/likeAdd.sql", "sqlInsertLike");
        jdbcTemplate.update(sqlInsertFriendship, filmId, id);

        return getFilmById(filmId);
    }

    @Override
    public Film deleteLike(long filmId, long id) {
        getFilmById(filmId);
        userStorage.getUserById(id);

        String sqlQuery = SqlFileReader.readSqlQuery("queries/like/likeDelete.sql", "deletelike");
        jdbcTemplate.update(sqlQuery, filmId, id);

        return getFilmById(filmId);
    }

    @Override
    public Collection<Film.Mpa> getAllMpa() {
        String sqlQuery = SqlFileReader.readSqlQuery("queries/mpa/getAllMpa.sql", "getAllMpa");
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    @Override
    public Film.Mpa getMpaById(long id) {
        String sqlQuery = SqlFileReader.readSqlQuery("queries/mpa/getMpaById.sql", "getMpaById");

        List<Film.Mpa> mpaList = jdbcTemplate.query(sqlQuery, this::mapRowToMpa, id);
        if (mpaList.size() != 1) {
            throw new NotFoundException("MPA с " + id + " не существует.");
        }
        return mpaList.getFirst();
    }

    @Override
    public Collection<Film.Genre> getAllGenre() {
        String sqlQuery = SqlFileReader.readSqlQuery("queries/genres/getAllGenre.sql", "getAllGenre");
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public Film.Genre getGenreById(long id) {
        String sqlQuery = SqlFileReader.readSqlQuery("queries/genres/getGenreById.sql", "getGenreById");

        List<Film.Genre> genreList = jdbcTemplate.query(sqlQuery, this::mapRowToGenre, id);
        if (genreList.size() != 1) {
            throw new NotFoundException("Жанр с " + id + " не существует.");
        }
        return genreList.getFirst();
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();

        film.setId(rs.getInt("film_id"));
        film.setName(rs.getString("film_name"));
        film.setDescription(rs.getString("film_description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        int mpaId = rs.getInt("mpa_id");
        String mpaName = rs.getString("mpa_Name");
        if (mpaId != 0) {
            film.setMpa(new Film.Mpa(mpaId, mpaName));
        }

        String likesStr = rs.getString("likes");
        if (likesStr != null && !likesStr.isEmpty()) {
            Arrays.stream(likesStr.split(","))
                    .map(Long::valueOf)
                    .forEach(film::like);
        }

        String genresStr = rs.getString("genres");
        if (genresStr != null && !genresStr.isEmpty() && !genresStr.equals(":")) {
            Arrays.stream(genresStr.split(","))
                    .map(genreStr -> {
                        String[] parts = genreStr.split(":");
                        if (parts.length > 0) {
                            int id = Integer.parseInt(parts[0]);
                            String name = parts.length > 1 ? parts[1] : null;
                            return new Film.Genre(id, name);
                        } else {
                            return null;
                        }
                    })
                    .forEach(film::addGenre);
        }

        return film;
    }

    private Film.Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        Film.Mpa filmMpa = new Film.Mpa();

        filmMpa.setId(rs.getInt("id"));
        filmMpa.setName(rs.getString("name"));

        return filmMpa;
    }

    private Film.Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        Film.Genre filmGenre = new Film.Genre();

        filmGenre.setId(rs.getInt("id"));
        filmGenre.setName(rs.getString("name"));

        return filmGenre;
    }

    private boolean doesMpaExist(int mpaId) {
        String mpaCheckQuery = SqlFileReader.readSqlQuery("queries/film/filmsCreate.sql", "mpaCheckQuery");
        Integer count = jdbcTemplate.queryForObject(mpaCheckQuery, Integer.class, mpaId);
        return count != null && count > 0;
    }

    private boolean doesGenreExist(int genreId) {
        String genreCheckQuery = SqlFileReader.readSqlQuery("queries/film/filmsCreate.sql", "genreCheckQuery");
        Integer count = jdbcTemplate.queryForObject(genreCheckQuery, Integer.class, genreId);
        return count != null && count > 0;
    }
}
