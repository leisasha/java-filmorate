-- getAllUsers
SELECT
    u.id AS user_id,
    u.login,
    u.name,
    u.birthday,
    e.email,
    GROUP_CONCAT(DISTINCT f.user_id_2 ORDER BY f.user_id_2 ASC) AS friends
FROM
    users u
LEFT JOIN
    emails e ON u.id = e.user_id
LEFT JOIN
    friendships f ON u.id = f.user_id_1
GROUP BY
    u.id;
--getUserById
SELECT
    u.id AS user_id,
    u.login,
    u.name,
    u.birthday,
    e.email,
    GROUP_CONCAT(DISTINCT f.user_id_2 ORDER BY f.user_id_2 ASC) AS friends
FROM
    users u
LEFT JOIN
    emails e ON u.id = e.user_id
LEFT JOIN
    friendships f ON u.id = f.user_id_1
WHERE
    u.id = ?
GROUP BY
    u.id;
