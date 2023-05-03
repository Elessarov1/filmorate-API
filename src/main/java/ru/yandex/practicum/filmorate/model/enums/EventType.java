package ru.yandex.practicum.filmorate.model.enums;

public enum EventType {
    LIKE("LIKE"),
    REVIEW("REVIEW"),
    FRIEND("FRIEND");

    private final String eventType;

    EventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "EventType{" +
                "eventType='" + eventType + '\'' +
                '}';
    }
}
