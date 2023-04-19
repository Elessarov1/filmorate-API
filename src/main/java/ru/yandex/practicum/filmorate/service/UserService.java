package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage storage) {
        this.storage = storage;
    }

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
        User user = storage.get(userId);
        User friend = storage.get(friendId);
        user.addFriend(friendId);
        return friend.addFriend(userId);
    }

    public boolean deleteFriend(int userId, int friendId) {
        User user = storage.get(userId);
        User friend = storage.get(friendId);
        user.deleteFriend(friendId);
        return friend.deleteFriend(userId);
    }

    public List<User> getAllFriends(int id) {
        User user = storage.get(id);
        return user.getFriends()
                .stream()
                .map(storage::get)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        List<User> userFriends = getAllFriends(userId);
        List<User> otherUserFriends = getAllFriends(otherId);
        List<User> commonFriends = new ArrayList<>(userFriends);
        commonFriends.retainAll(otherUserFriends);
        return commonFriends;
    }
}
