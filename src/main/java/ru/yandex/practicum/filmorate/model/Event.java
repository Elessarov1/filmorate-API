package ru.yandex.practicum.filmorate.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;


@Data
@Builder
@Schema(description = "Information about all user actions")
public class Event {
    private final Long timestamp;
    private final int userId;
    private final EventType eventType;
    private final Operation operation;
    private final int entityId;
    private int eventId;
}
