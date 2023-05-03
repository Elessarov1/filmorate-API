package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static ru.yandex.practicum.filmorate.model.enums.EventType.FRIEND;
import static ru.yandex.practicum.filmorate.model.enums.Operation.ADD;
import static ru.yandex.practicum.filmorate.model.enums.Operation.REMOVE;

@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    private final EventStorage eventStorage;

    @Override
    public User add(User user) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        Map<String, Object> userFields = getUserFields(user);
        Number key = jdbcInsert.withTableName("USERS")
                .usingGeneratedKeyColumns("ID")
                .executeAndReturnKey(userFields);
        user.setId(key.intValue());
        user.setName(String.valueOf(userFields.get("NAME")));

        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        List<Map<String, Object>> batchValues = new ArrayList<>();
        for (Integer friend : user.getFriends()) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("USER_ID", user.getId());
            parameters.put("FRIEND_ID", friend);
            batchValues.add(parameters);
        }
        jdbcInsert.withTableName("FRIENDS")
                .executeBatch(batchValues.toArray(new Map[batchValues.size()]));
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
        List<Object> friends = Arrays.asList(user.getFriends().toArray());
        if (!friends.isEmpty()) {
            batchInsert(user, friends, "INSERT INTO FRIENDS VALUES (?,?)");
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
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToUser, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("No such user in the database");
        }
    }

    @Override
    public boolean addFriend(int userId, int friendId) {
        int updatedRows = 0;
        if (friendCheck(userId, friendId)) {
            String sql = "UPDATE FRIENDS SET FRIENDSHIP_STATUS = ? WHERE USER_ID = ? AND FRIEND_ID = ?";
            updatedRows = jdbcTemplate.update(sql, "TRUE", userId, friendId);
        } else {
            String sql = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID, FRIENDSHIP_STATUS) VALUES (?,?,?)";
            updatedRows = jdbcTemplate.update(sql, userId, friendId, "FALSE");

            Event event = Event.builder()
                    .timestamp(System.currentTimeMillis())
                    .userId(userId)
                    .eventType(FRIEND)
                    .operation(ADD)
                    .entityId(friendId)
                    .build();
            eventStorage.add(event);
        }
        if (updatedRows > 0) {
            return true;
        }
        throw new NotFoundException("No such users in the database");
    }

    @Override
    public boolean deleteFriend(int userId, int friendId) {
        String sql = "DELETE FROM FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?";
        Event event = Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(userId)
                .eventType(FRIEND)
                .operation(REMOVE)
                .entityId(friendId)
                .build();
        eventStorage.add(event);
        return jdbcTemplate.update(sql, userId, friendId) > 0;
    }

    @Override
    public List<User> getAllFriends(int id) {
        if (get(id) == null) {
            throw new NotFoundException("No such user in the database");
        }
        String sql = "SELECT * FROM USERS WHERE ID IN (SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?)";
        return jdbcTemplate.query(sql, this::mapRowToUser, id);
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        String sql = "SELECT * FROM USERS WHERE ID IN (SELECT f1.FRIEND_ID FROM FRIENDS f1 " +
                "JOIN FRIENDS f2 ON f1.FRIEND_ID = f2.FRIEND_ID " +
                "WHERE f1.USER_ID = ? AND f2.USER_ID = ?)";
        return jdbcTemplate.query(sql, this::mapRowToUser, userId, otherId);
    }

    private Map<String, Object> getUserFields(User user) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("LOGIN", user.getLogin());
        if (user.getName().isEmpty()) {
            fields.put("NAME", user.getLogin());
        } else {
            fields.put("NAME", user.getName());
        }
        fields.put("EMAIL", user.getEmail());
        fields.put("BIRTHDAY", user.getBirthday());
        return fields;
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
        while (rowSet.next()) {
            res.add(rowSet.getInt("FRIEND_ID"));
        }
        return res;
    }

    private int[] batchInsert(User user, List<Object> list, String sql) {
        return jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, user.getId());
                ps.setObject(2, list.get(i));
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        });
    }

    private boolean friendCheck(int userId, int friendId) {
        if (userId < 0 || friendId < 0) {
            throw new NotFoundException("Invalid Data");
        }
        String sql = "SELECT COUNT(*) FROM FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{userId, friendId}, Integer.class) > 0;
    }
}
