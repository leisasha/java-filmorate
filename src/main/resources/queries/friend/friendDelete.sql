--deleteFriendQuery
DELETE FROM friendships
WHERE (user_id_1 = ? AND user_id_2 = ?);
