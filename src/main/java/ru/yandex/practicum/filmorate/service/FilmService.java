package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.enums.SearchQueryArgs;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.enums.SearchQueryArgs.DIRECTOR;
import static ru.yandex.practicum.filmorate.enums.SearchQueryArgs.TITLE;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {
    @Qualifier("filmDbStorage")
    private final FilmStorage storage;
    @Qualifier("directorDbStorage")
    private final DirectorStorage directorStorage;

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

    public List<Film> getMostLikedFilms(int count, int genreId, int year) {
        if ((genreId == 0) && (year == 0)) {
            return getAllFilms()
                    .stream()
                    .sorted(Comparator.comparingInt(Film::getLikesCount)
                            .reversed())
                    .limit(count)
                    .collect(Collectors.toList());
        }
        if ((genreId > 0) || (year > 0)) {
            return storage.getByGenreOrYear(count, genreId, year);
        }
        return Collections.emptyList();
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        return storage.getCommonFilms(userId, friendId);
    }

    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        Director validDirector = directorStorage.getById(directorId);
        List<Film> films = storage.getFilmsByDirector(validDirector.getId());
        switch (sortBy.toLowerCase()) {
            case "year":
                films.sort(Comparator.comparingInt(f -> f.getReleaseDate().getYear()));
                break;
            case "likes":
                films.sort(Comparator.comparing(Film::getLikesCount));
                break;
        }
        return films;
    }

    public List<Film> getByTitleOrDirector(String query, List<String> by) {
        String lowerCaseQuery = query.toLowerCase();
        if (by.contains(DIRECTOR.getLowerCase()) && by.contains(TITLE.getLowerCase())) {
            return storage.findByRequestedTitleAndDirector(lowerCaseQuery);
        }
        String enumName = by.get(0).toUpperCase();
        SearchQueryArgs argumentValue;
        try {
            argumentValue = SearchQueryArgs.valueOf(enumName);
        } catch (IllegalArgumentException e) {
            log.warn("Указан неверный параметр поиска: {}", enumName);
            return Collections.emptyList();
        }
        switch (argumentValue) {
            case DIRECTOR:
                return storage.findByRequestedDirector(lowerCaseQuery);
            case TITLE:
                return storage.findByRequestedTitle(lowerCaseQuery);
            default:
                return Collections.emptyList();
        }
    }
}