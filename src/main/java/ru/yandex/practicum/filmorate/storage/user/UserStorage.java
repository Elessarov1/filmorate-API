package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User add(User user);
    User update(User user);
    List<User> getAllUsers();
    boolean delete(int id);
    User get(int id);

    boolean addFriend(int userId, int friendId);
    boolean deleteFriend(int userId, int friendId);
    List<User> getAllFriends(int id);
    List<User> getCommonFriends(int userId, int otherId);
}
