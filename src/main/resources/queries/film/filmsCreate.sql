--createFilm
insert into films (
    name, description, release_date, duration, mpa_id
)
values (?, ?, ?, ?, ?)
--genreQuery
insert into film_genres (film_id, genre_id)
values (?, ?);
--likeQuery
insert into likes (film_id, user_id)
values (?, ?)
--mpaCheckQuery
SELECT COUNT(*) FROM mpa WHERE id = ?
--genreCheckQuery
SELECT COUNT(*) FROM genres WHERE id = ?