package ru.yandex.practicum.filmorate.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "Genre of film")
public class Genre {
    private int id;
    private String name;
}
