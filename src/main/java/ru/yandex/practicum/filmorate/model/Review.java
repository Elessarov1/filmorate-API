package ru.yandex.practicum.filmorate.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "Information about film reviews")
public class Review {
    @NotNull
    private int reviewId;

    @NotNull
    private String content;

    @NotNull
    private Boolean isPositive;

    private Integer userId;

    private Integer filmId;

    @NotNull
    private int useful;
}
