package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    @NotBlank
    private final String login;
    @Email
    private final String email;
    @PastOrPresent
    private final LocalDate birthday;
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
