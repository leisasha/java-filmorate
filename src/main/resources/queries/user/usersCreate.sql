--sqlQuery
INSERT INTO users (login, name, birthday) VALUES (?, ?, ?);
--emailQuery
INSERT INTO emails (user_id, email) VALUES (?, ?);