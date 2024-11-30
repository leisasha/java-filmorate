--updateFilms
update films
SET
    name = ?,
    description = ?,
    release_date = ?,
    duration = ?,
    mpa_id = ?
WHERE id = ?;
--deleteGenresQuery
DELETE FROM film_genres WHERE film_id = ?;
--insertGenresQuery
INSERT INTO film_genres (film_id, genre_id)
VALUES (?, ?);
--deleteLikesQuery
DELETE FROM likes WHERE film_id = ?;
--insertLikesQuery
INSERT INTO likes (film_id, user_id)
VALUES (?, ?);