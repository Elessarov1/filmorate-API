package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film add(Film film);

    Film update(Film film);

    List<Film> getAllFilms();

    boolean delete(int id);

    Film get(int id);

    boolean addLikeToFilm(int filmId, int userId);

    boolean deleteLike(int filmId, int userId);
}
