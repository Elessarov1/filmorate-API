package ru.yandex.practicum.filmorate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Methods for working with users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Information about all users in the database")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Information about user by personal ID")
    public User getUser(@Parameter(description = "Unique user id") @PathVariable int id) {
        return userService.getUser(id);
    }

    @PostMapping
    @Operation(summary = "Add user in the database")
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping
    @Operation(summary = "Update user in the database")
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user from the database")
    public boolean deleteUser(@Parameter(description = "Unique user id") @PathVariable int id) {
        return userService.deleteUser(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @Operation(summary = "Add another user to friends")
    public boolean addToFriends(@Parameter(description = "User making the request") @PathVariable int id,
                                @Parameter(description = "User receiving the request") @PathVariable int friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @Operation(summary = "Delete user from friends")
    public boolean deleteFriend(@Parameter(description = "User making the request") @PathVariable int id,
                                @Parameter(description = "Removable user") @PathVariable int friendId) {
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    @Operation(summary = "Information about user by personal ID")
    public List<User> getAllFriends(@Parameter(description = "") @PathVariable int id) {
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @Operation(summary = "Information about common friends between two users")
    public List<User> getCommonFriends(@PathVariable int id,
                                       @PathVariable int otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/feed")
    @Operation(summary = "Information about all friends actions by personal ID")
    public List<Event> getFeed(@Parameter(description = "Unique user id") @PathVariable int id) {
        return userService.getFeed(id);
    }
}
