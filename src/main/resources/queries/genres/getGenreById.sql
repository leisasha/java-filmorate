--getGenreById
SELECT ID, NAME
FROM genres
WHERE id = ?
ORDER BY id;