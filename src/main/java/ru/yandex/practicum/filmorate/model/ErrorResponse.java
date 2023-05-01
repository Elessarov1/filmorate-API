package ru.yandex.practicum.filmorate.model;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
public class ErrorResponse {
    private final String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}