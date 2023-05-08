package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    Director create(Director director);

    Director update(Director director);

    void delete(int directorId);

    List<Director> getAllDirectors();

    Director getById(int directorId);
}
