package ru.yandex.practicum.filmorate.storage.user;

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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.utility.SqlFileReader;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Primary
@Component
@Slf4j
@RequiredArgsConstructor
public class DbUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @SneakyThrows
    @Transactional
    @Override
    public User create(User user) {
        if (doesEmailExist(user.getEmail())) {
            throw new ValidationException("Email уже занят: " + user.getEmail());
        }
        if (doesLoginExist(user.getLogin())) {
            throw new ValidationException("Login уже занят: " + user.getLogin());
        }

        String sqlQuery = SqlFileReader.readSqlQuery("queries/user/usersCreate.sql", "sqlQuery");
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getName());
            stmt.setDate(3, Date.valueOf(user.getBirthday()));

            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());

        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            String emailQuery = SqlFileReader.readSqlQuery("queries/user/usersCreate.sql", "emailQuery");
            jdbcTemplate.update(emailQuery, user.getId(), user.getEmail());
        }

        return user;
    }

    @Transactional
    @Override
    public User update(User newUser) {
        String sqlQuery = SqlFileReader.readSqlQuery("queries/user/usersUpdate.sql", "updateUsers");

        int resultQuery = jdbcTemplate.update(
                sqlQuery,
                newUser.getLogin(),
                newUser.getName(),
                newUser.getBirthday(),
                newUser.getId()
        );

        if (resultQuery != 1) {
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        if (newUser.getEmail() != null && !newUser.getEmail().isEmpty()) {
            String deleteEmailQuery = SqlFileReader.readSqlQuery("queries/user/usersUpdate.sql", "deleteEmailQuery");
            jdbcTemplate.update(deleteEmailQuery, newUser.getId());
            String insertEmailQuery = SqlFileReader.readSqlQuery("queries/user/usersUpdate.sql", "insertEmailQuery");
            jdbcTemplate.update(insertEmailQuery, newUser.getEmail(), newUser.getId());
        }

        return newUser;
    }

    @Override
    public Collection<User> getAll() {
        String sqlQuery = SqlFileReader.readSqlQuery("queries/user/usersSelect.sql", "getAllUsers");
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User getUserById(long id) {
        String sqlQuery = SqlFileReader.readSqlQuery("queries/user/usersSelect.sql", "getUserById");

        List<User> users = jdbcTemplate.query(sqlQuery, this::mapRowToUser, id);
        if (users.isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

        return users.getFirst();
    }

    @Override
    public User addFriend(long id, long friendId) {
        getUserById(id);
        getUserById(friendId);

        String sqlInsertFriendship = SqlFileReader.readSqlQuery("queries/friend/friendsAdd.sql", "sqlInsertFriendship");
        jdbcTemplate.update(sqlInsertFriendship, id, friendId);

        return getUserById(id);
    }

    @Override
    public Collection<User> getUserFriends(long id) {
        getUserById(id);
        String sqlQuery = SqlFileReader.readSqlQuery("queries/friend/friendsGet.sql", "sqlQuery");
        Collection<Long> friendsId = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getLong("user_id_2"), id);

        return friendsId.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    @Override
    public User deleteFriend(long id, long friendId) {
        getUserById(id);
        getUserById(friendId);

        String sqlQuery = SqlFileReader.readSqlQuery("queries/friend/friendDelete.sql", "deleteFriendQuery");
        jdbcTemplate.update(sqlQuery, id, friendId);

        return getUserById(id);
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();

        user.setId(rs.getLong("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());

        String friendsStr = rs.getString("friends");
        if (friendsStr != null && !friendsStr.isEmpty()) {
            Arrays.stream(friendsStr.split(","))
                    .map(Long::valueOf)
                    .forEach(user::addFriend);
        }

        return user;
    }

    private boolean doesEmailExist(String email) {
        String query = "SELECT COUNT(*) FROM emails WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, email);
        return count != null && count > 0;
    }

    private boolean doesLoginExist(String login) {
        String query = "SELECT COUNT(*) FROM users WHERE login = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, login);
        return count != null && count > 0;
    }
}
