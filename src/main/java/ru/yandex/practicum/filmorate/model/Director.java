package ru.yandex.practicum.filmorate.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@Schema(description = "Information about film directors")
public class Director {
    private int id;
    @NotBlank
    private String name;
}