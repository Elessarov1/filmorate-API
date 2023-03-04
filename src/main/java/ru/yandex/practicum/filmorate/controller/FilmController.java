package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private int filmId = 1;
    private final HashMap<Integer, Film> films = new HashMap<>();
    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }
    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        validateFilm(film);
        films.put(film.getId(), film);
        log.info("Фильм добавлен в список");
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Фильма не было в списке");
            throw new NotFoundException("Film not found");
        }
        films.put(film.getId(), film);
        log.info("Фильм успешно обновлен");
        return film;
    }
    private void validateFilm(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))) {
            log.info("Дата указана не верно");
            throw new ValidationException("invalid release date");
        }
        if (film.getId() != 0) {
            log.warn("В теле запроса указан id");
            throw new ValidationException("Bad request");
        }
        film.setId(filmId++);
        log.info("Валидация фильма прошла успешно");
    }

}
