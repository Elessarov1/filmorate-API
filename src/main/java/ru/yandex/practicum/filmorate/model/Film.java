package ru.yandex.practicum.filmorate.model;

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
public class Film {
    private int id;
    @NotBlank
    private final String name;
    @Size(min = 1, max = 200)
    private final String description;
    private final LocalDate releaseDate;
    @Positive
    private final int duration;
    private final Set<Integer> likes = new HashSet<>();

    public boolean addLike(int id) {
        return likes.add(id);
    }

    public boolean deleteLike(int id) {
        return likes.remove(id);
    }

    public int getLikesCount() {
        return likes.size();
    }
}
