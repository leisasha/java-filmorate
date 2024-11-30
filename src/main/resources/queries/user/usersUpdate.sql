-- updateUsers
UPDATE users SET login = ?, name = ?, birthday = ? WHERE id = ?;
-- deleteEmailQuery
DELETE FROM emails WHERE user_id = ?;
-- insertEmailQuery
INSERT INTO emails (email, user_id) VALUES (?, ?);
