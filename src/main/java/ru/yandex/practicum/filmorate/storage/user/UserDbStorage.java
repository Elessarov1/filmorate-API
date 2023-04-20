package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User add(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        Number key = simpleJdbcInsert.withTableName("USERS")
                .usingGeneratedKeyColumns("ID")
                .executeAndReturnKey(getUserFields(user));
        if (!user.getFriends().isEmpty()) {
            simpleJdbcInsert.withTableName("FRIENDS")
                    .execute(getUserFriends(user));
        }
        user.setId(key.intValue());
        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE USERS SET LOGIN = ?, NAME = ?, EMAIL = ?, BIRTHDAY = ? WHERE ID = ?";
        int rowsUpdated = jdbcTemplate.update(sql,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday(),
                user.getId()
        );
        if (rowsUpdated == 0) {
            throw new NotFoundException("No such user in the database");
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM USERS";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM USERS WHERE ID = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    @Override
    public User get(int id) {
        String sql = "SELECT * FROM USERS WHERE ID = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToUser, id);
    }

    private Map<String, Object> getUserFields(User user) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("LOGIN", user.getLogin());
        fields.put("NAME", user.getName());
        fields.put("EMAIL", user.getEmail());
        fields.put("BIRTHDAY", user.getBirthday());
        return fields;
    }

    private Map<String, Object> getUserFriends(User user) {
        Map<String, Object> friends = new HashMap<>();
        user.getFriends().forEach(
                friend -> friends.put(String.valueOf(user.getId()), friend)
        );
        return friends;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = User.builder()
                .id(resultSet.getInt("ID"))
                .login(resultSet.getString("LOGIN"))
                .name(resultSet.getString("NAME"))
                .email(resultSet.getString("EMAIL"))
                .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
                .build();
        user.getFriends().addAll(getFriendsByUserId(resultSet.getInt("ID")));
        return user;
    }
    private Set<Integer> getFriendsByUserId(int id) {
        String sql = "SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        Set<Integer> res = new HashSet<>();
        while(rowSet.next()) {
            res.add(rowSet.getInt("FRIEND_ID"));
        }
        return res;
    }
}
