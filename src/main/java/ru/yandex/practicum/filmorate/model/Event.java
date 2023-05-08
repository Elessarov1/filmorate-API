package ru.yandex.practicum.filmorate.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;


@Data
@AllArgsConstructor
@Builder
@Schema(description = "Information about all user actions")
public class Event {
    private Long timestamp;
    private int userId;
    private EventType eventType;
    private Operation operation;
    private int entityId;
    private int eventId;
}
