package ru.yandex.practicum.filmorate.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "Information about user")
public class User {
    @NotBlank
    private String login;
    @Email
    private String email;
    @PastOrPresent
    private LocalDate birthday;
    private final Set<Integer> friends = new HashSet<>();
    private int id;
    private String name;

    public boolean addFriend(int id) {
        return friends.add(id);
    }

    public boolean deleteFriend(int id) {
        return friends.remove(id);
    }
}
