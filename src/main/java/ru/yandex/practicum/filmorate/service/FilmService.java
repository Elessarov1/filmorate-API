package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage storage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage storage,
            @Qualifier("userDbStorage") UserStorage userStorage
    ) {
        this.storage = storage;
        this.userStorage = userStorage;
    }

    public List<Film> getAllFilms() {
        return storage.getAllFilms();
    }

    public Film getFilm(int id) {
        return storage.get(id);
    }

    public Film addFilm(Film film) {
        return storage.add(film);
    }

    public Film updateFilm(Film film) {
        return storage.update(film);
    }

    public boolean deleteFilm(int id) {
        return storage.delete(id);
    }

    public boolean addLikeToFilm(int filmId, int userId) {
        return storage.addLikeToFilm(filmId, userId);
    }

    public boolean deleteLike(int userId, int filmId) {
        return storage.deleteLike(filmId, userId);
    }

    public List<Film> getMostLikedFilms(int count) {
        return getAllFilms()
                .stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount)
                        .reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
