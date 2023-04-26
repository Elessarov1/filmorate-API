package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
@Qualifier("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private final UserStorage userStorage = new InMemoryUserStorage();
    private int filmId = 1;

    @Override
    public Film add(Film film) {
        validateFilm(film);
        films.put(film.getId(), film);
        log.info("Film added to list");
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Film doesn't exist");
            throw new NotFoundException("Film not found");
        }
        films.put(film.getId(), film);
        log.info("Film updated successfully");
        return film;
    }

    @Override
    public boolean delete(int id) {
        if (films.containsKey(id)) {
            films.remove(id);
            log.info("Film removed successfully");
            return true;
        }
        log.warn("Film doesn't exist");
        throw new NotFoundException("Film doesn't exist");
    }

    @Override
    public Film get(int id) {
        if (films.containsKey(id)) {
            return films.get(id);
        }
        log.warn("Film doesn't exist");
        throw new NotFoundException("Film doesn't exist");
    }

    @Override
    public boolean addLikeToFilm(int filmId, int userId) {
        if (!(userStorage.get(userId) == null)) {
            Film film = films.get(filmId);
            return true;
        }
        throw new NotFoundException("Invalid user id");
    }

    @Override
    public boolean deleteLike(int filmId, int userId) {
        if (userId > 0) {
            Film film = films.get(filmId);
            return true;
        }
        throw new NotFoundException("Invalid user id");
    }

    @Override
    public List<Film> getFilmsByDirector(int directorId) {
        return null;
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    private void validateFilm(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("The date is incorrect");
            throw new ValidationException("invalid release date");
        }
        if (film.getId() != 0) {
            log.warn("Request body has id field");
            throw new ValidationException("Bad request");
        }
        film.setId(filmId++);
        log.info("Validation successful");
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        return null;
    }

    @Override
    public List<Film> findByRequestedDirector(String query) {
        return null;
    }

    @Override
    public List<Film> findByRequestedTitle(String query) {
        return null;
    }

    @Override
    public List<Film> findByRequestedTitleAndDirector(String query) {
        return null;
    }
}
