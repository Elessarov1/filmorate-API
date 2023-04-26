package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public List<Director> getAll() {
        return null;
    }

    public Director getById(int directorId) {
        return null;
    }

    public Director createDirector(Director director) {
        return null;
    }

    public Director updateDirector(Director director) {
        return null;
    }

    public void deleteDirector(int directorId) {

    }
}
