package ru.yandex.practicum.filmorate.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "Information about film")
public class Film {
    @Size(min = 1, max = 200)
    private final String description;
    @Positive
    private final int duration;
    private final LocalDate releaseDate;
    private final Mpa mpa;
    private final Set<Director> directors = new HashSet<>();
    private final Set<Integer> likes = new HashSet<>();
    private final Set<Genre> genres = new HashSet<>();
    private int id;
    @NotBlank
    private String name;

    public boolean deleteLike(int id) {
        return likes.remove(id);
    }

    public boolean addGenre(Genre genre) {
        return genres.add(genre);
    }

    public boolean deleteGenre(Genre genre) {
        return genres.remove(genre);
    }

    public int getLikesCount() {
        return likes.size();
    }
}
