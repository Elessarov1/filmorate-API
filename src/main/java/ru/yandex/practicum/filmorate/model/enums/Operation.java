package ru.yandex.practicum.filmorate.model.enums;

public enum Operation {
    REMOVE("REMOVE"),
    ADD("ADD"),
    UPDATE("UPDATE");

    private final String operation;

    Operation(String operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return operation;
    }
}
