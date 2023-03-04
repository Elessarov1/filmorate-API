package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
@Builder
public class User {
    private int id;
    @NotBlank
    private final String login;
    private String name;
    @Email
    private final String email;
    @PastOrPresent
    private final LocalDate birthday;
}
