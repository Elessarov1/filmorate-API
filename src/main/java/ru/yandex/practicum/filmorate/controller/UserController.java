package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private int userId = 1;
    private final HashMap<Integer, User> users = new HashMap<>();
    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        validateUser(user);
        users.put(user.getId(), user);
        log.info("Пользователь добавлен в список");
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Пользователя нет в списке");
            throw new NotFoundException("User not found");
        }
        users.put(user.getId(), user);
        log.info("Пользователь успешно обновлен");
        return user;
    }

    private void validateUser(User user) throws ValidationException {
        if (user.getName() == null) {
            user.setName(user.getLogin());
            log.info("Присвоен логин в качестве имени пользователя");
        }
        if (user.getId() != 0) {
            log.warn("В теле запроса был указан id");
            throw new ValidationException("Bad request");
        }
        user.setId(userId++);
        log.info("Валидация пользователя прошла успешно");
    }
}
