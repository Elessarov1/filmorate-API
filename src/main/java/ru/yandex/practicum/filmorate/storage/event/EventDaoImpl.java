package ru.yandex.practicum.filmorate.storage.event;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class EventDaoImpl implements EventDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> getFeed(int userId) {
        String sql = "SELECT * FROM EVENTS WHERE USER_ID IN (SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?)";
        if (isExist(userId)) {
            return jdbcTemplate.query(sql, this::mapRowToEvent, userId);
        }
        throw new NotFoundException("No such user in the database");
    }

    @Override
    public Event add(Event event) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        Map<String, Object> eventFields = getEventFields(event);
        int id = jdbcInsert.withTableName("EVENTS")
                .usingGeneratedKeyColumns("EVENT_ID")
                .executeAndReturnKey(eventFields).intValue();
        event.setId(id);
        return event;
    }

    private Map<String, Object> getEventFields(Event event) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("DATE_TIME", event.getTimestamp());
        fields.put("USER_ID", event.getUserId());
        fields.put("EVENT_TYPE", event.getEventType().name());
        fields.put("OPERATION", event.getOperation().name());
        fields.put("ENTITY_ID", event.getEntityId());
        return fields;
    }

    private Event mapRowToEvent(ResultSet resultSet, int rowNum) throws SQLException {
        Event event = Event.builder()
                .id(resultSet.getInt("EVENT_ID"))
                .timestamp(resultSet.getTimestamp("DATE_TIME").toLocalDateTime())
                .userId(resultSet.getInt("USER_ID"))
                .eventType(EventType.valueOf(resultSet.getString("EVENT_TYPE")))
                .operation(Operation.valueOf(resultSet.getString("OPERATION")))
                .entityId(resultSet.getInt("ENTITY_ID"))
                .build();
        return event;
    }

    private boolean isExist(int userId) {
        String sql = "SELECT COUNT(*) FROM USERS WHERE ID = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{userId}, Integer.class) > 0;
    }
}
