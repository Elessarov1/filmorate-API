package ru.yandex.practicum.filmorate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
@Tag(name = "Directors", description = "Methods for working with directors")
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    @Operation(summary = "Information about all directors in the database")
    public List<Director> getAllDirectors() {
        return directorService.getAll();
    }

    @GetMapping("/{directorId}")
    @Operation(summary = "Information about director by personal ID")
    public Director getDirectorById(@Parameter(description = "Unique director id") @PathVariable int directorId) {
        return directorService.getById(directorId);
    }

    @PostMapping
    @Operation(summary = "Add director in the database")
    public Director addDirector(@Valid @RequestBody Director director) {
        return directorService.createDirector(director);
    }

    @PutMapping
    @Operation(summary = "Update director in the database")
    public Director updateDirector(@Valid @RequestBody Director director) {
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{directorId}")
    @Operation(summary = "Delete director from database")
    public void deleteDirector(@PathVariable int directorId) {
        directorService.deleteDirector(directorId);
    }
}
