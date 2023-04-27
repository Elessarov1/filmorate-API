package ru.yandex.practicum.filmorate.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "Information about all user actions")
public class Event {
    private final LocalDateTime timestamp;
    private final int userId;
    private final EventType eventType;
    private final Operation operation;
    private final int entityId;
    private int id;
}
