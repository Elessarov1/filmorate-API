package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage storage;
    private final UserStorage userStorage;
    private final DirectorStorage directorStorage;

    @Autowired
    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage storage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            @Qualifier("directorDbStorage") DirectorStorage directorStorage
    ) {
        this.storage = storage;
        this.userStorage = userStorage;
        this.directorStorage = directorStorage;
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

    public List<Film> getCommonFilms(long userId, long friendId) {
        return storage.getCommonFilms(userId, friendId);
    }

    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        Director validDirector = directorStorage.getById(directorId);
        List<Film> films = storage.getFilmsByDirector(validDirector.getId());
        switch (sortBy) {
            case "year":
                films.sort(Comparator.comparingInt(f -> f.getReleaseDate().getYear()));
                return films;
            case "likes":
                films.sort(Comparator.comparing(Film::getLikesCount));
                return films;
        }
        return films;
    }

    public List<Film> getByTitleOrDirector(String query, String[] by) {
        int titleAndDirectorArgs = 2;
        String lowerCaseQuery = query.toLowerCase();
        String argumentValue = by[0];

        if (by.length == titleAndDirectorArgs) {
            return storage.findByRequestedTitleAndDirector(lowerCaseQuery);
        }
        switch (argumentValue) {
            case "director":
                return storage.findByRequestedDirector(lowerCaseQuery);
            case "title":
                return storage.findByRequestedTitle(lowerCaseQuery);
            default:
                return Collections.emptyList();
        }
    }
}