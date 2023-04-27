package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventDao {
    List<Event> getFeed(int userId);

    Event add(Event event);
}
