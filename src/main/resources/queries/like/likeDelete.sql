--deletelike
DELETE FROM likes
WHERE (film_id = ? AND user_id = ?);
