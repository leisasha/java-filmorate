-- getAllFilms
select
    f.id as film_id,
    f.name as film_name,
    f.description as film_description,
    f.release_date as release_date,
    f.duration as duration,
    m.id as mpa_id,
    m.name as mpa_Name,
    GROUP_CONCAT(DISTINCT CONCAT(g.id, ':', g.name) ORDER BY g.id ASC) AS genres,
    GROUP_CONCAT(DISTINCT l.user_id ORDER BY l.user_id ASC) AS likes
FROM
    films f
LEFT JOIN
    mpa m ON f.mpa_id = m.id
LEFT JOIN
    film_genres fg ON f.id = fg.film_id
LEFT JOIN
    genres g ON fg.genre_id = g.id
LEFT JOIN
    likes l ON f.id = l.film_id
GROUP BY
    f.id
-- getFilmById
SELECT
    f.id AS film_id,
    f.name AS film_name,
    f.description AS film_description,
    f.release_date AS release_date,
    f.duration AS duration,
    m.id AS mpa_id,
    m.name as mpa_Name,
    GROUP_CONCAT(DISTINCT CONCAT(g.id, ':', g.name) ORDER BY g.id ASC) AS genres,
    GROUP_CONCAT(DISTINCT l.user_id ORDER BY l.user_id ASC) AS likes
FROM
    films f
LEFT JOIN
    mpa m ON f.mpa_id = m.id
LEFT JOIN
    film_genres fg ON f.id = fg.film_id
LEFT JOIN
    genres g ON fg.genre_id = g.id
LEFT JOIN
    likes l ON f.id = l.film_id
WHERE
    f.id = ?
GROUP BY
    f.id;
