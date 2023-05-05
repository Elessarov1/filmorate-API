package ru.yandex.practicum.filmorate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
@Tag(name = "Genres", description = "Methods for working with genres")
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    @Operation(summary = "Information about all genres in the database")
    public List<Genre> getAllGenres() {
        return genreService.getAllGenres();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Information about genre by personal ID")
    public Genre getGenre(@Parameter(description = "Unique genre id") @PathVariable int id) {
        return genreService.getGenre(id);
    }
}
