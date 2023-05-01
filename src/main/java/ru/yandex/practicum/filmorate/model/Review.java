package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
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
