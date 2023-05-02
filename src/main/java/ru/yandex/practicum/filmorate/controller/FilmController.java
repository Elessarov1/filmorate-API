package ru.yandex.practicum.filmorate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Tag(name = "Films", description = "Methods for working with films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    @Operation(summary = "Information about all films in the database")
    public List<Film> getAllFilms() {
            return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Information about film by personal ID")
    public Film getFilm(@Parameter(description = "Unique film id") @PathVariable int id) {
        return filmService.getFilm(id);
    }

    @PostMapping
    @Operation(summary = "Add film in the database")
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    @Operation(summary = "Update film in the database")
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete film from database")
    public boolean deleteFilm(@Parameter(description = "Unique film id") @PathVariable int id) {
        return filmService.deleteFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    @Operation(summary = "Like the film from the user")
    public boolean addLike(@Parameter(description = "Liked film") @PathVariable int id,
                           @Parameter(description = "User who liked") @PathVariable int userId) {
        return filmService.addLikeToFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @Operation(summary = "Remove like from film")
    public boolean deleteLike(@Parameter(description = "Unliked film") @PathVariable int id,
                              @Parameter(description = "User who unliked") @PathVariable int userId) {
        return filmService.deleteLike(userId, id);
    }

    @GetMapping("/popular")
    public List<Film> getMostLikedFilms(
            @RequestParam(defaultValue = "10", required = false) @PathVariable int count,
            @RequestParam(defaultValue = "0") int genreId,
            @RequestParam(defaultValue = "0") int year) {
        return filmService.getMostLikedFilms(count, genreId, year);
    }

    @GetMapping("/common")
    @Operation(summary = "Get common films with another user")
    public List<Film> getCommonFilms(@RequestParam long userId, @RequestParam long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/director/{directorId}")
    @Operation(summary = "Get films by director ID")
    public List<Film> getByDirector(@PathVariable int directorId,
                                    @RequestParam(defaultValue = "year") String sortBy) {
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    @GetMapping("/search")
    @Operation(summary = "Find films by director ID or title")
    public List<Film> getByTitleOrDirector(@RequestParam String query, @RequestParam List<String> by) {
        return filmService.getByTitleOrDirector(query, by);
    }
}