package ru.yandex.practicum.filmorate.enums;

public enum SearchQueryArgs {
    DIRECTOR("director"),
    TITLE("title");

    private final String query;

    SearchQueryArgs(String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return query;
    }
}