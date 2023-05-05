package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    @Qualifier("userDbStorage")
    private final UserStorage storage;
    private final EventStorage eventStorage;

    public List<User> getAllUsers() {
        return storage.getAllUsers();
    }

    public User getUser(int id) {
        return storage.get(id);
    }

    public User addUser(User user) {
        return storage.add(user);
    }

    public User updateUser(User user) {
        return storage.update(user);
    }

    public boolean deleteUser(int id) {
        return storage.delete(id);
    }

    public boolean addFriend(int userId, int friendId) {
        return storage.addFriend(userId, friendId);
    }

    public boolean deleteFriend(int userId, int friendId) {
        return storage.deleteFriend(userId, friendId);
    }

    public List<User> getAllFriends(int id) {
        return storage.getAllFriends(id);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        return storage.getCommonFriends(userId, otherId);
    }

    public List<Event> getFeed(int userId) {
        return eventStorage.getFeed(userId);
    }
}
