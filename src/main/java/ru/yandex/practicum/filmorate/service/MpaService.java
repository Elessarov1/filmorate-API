package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage storage;

    public List<Mpa> getAllMpa() {
        return storage.getAllMpa();
    }

    public Mpa getMpa(int id) {
        return storage.get(id);
    }
}
