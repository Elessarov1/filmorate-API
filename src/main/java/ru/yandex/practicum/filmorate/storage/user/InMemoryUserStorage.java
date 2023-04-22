package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private int userId = 1;
    private final HashMap<Integer, User> users = new HashMap<>();
    @Override
    public User add(User user) {
        validateUser(user);
        users.put(user.getId(), user);
        log.info("User added successful");
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("The user is not in the list");
            throw new NotFoundException("User not found");
        }
        users.put(user.getId(), user);
        log.info("User updated successfully");
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean delete(int id) {
        if (users.containsKey(id)) {
            users.remove(id);
            log.info("User removed successfully");
            return true;
        }
        log.warn("User doesn't exist");
        throw new NotFoundException("User doesn't exist");
    }

    @Override
    public User get(int id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        log.warn("User doesn't exist");
        throw new NotFoundException("User doesn't exist");
    }

    @Override
    public boolean addFriend(int userId, int friendId) {
        User user = this.get(userId);
        User friend = this.get(friendId);
        user.addFriend(friendId);
        return friend.addFriend(userId);
    }

    @Override
    public boolean deleteFriend(int userId, int friendId) {
        User user = this.get(userId);
        User friend = this.get(friendId);
        user.deleteFriend(friendId);
        return friend.deleteFriend(userId);
    }

    @Override
    public List<User> getAllFriends(int id) {
        User user = this.get(id);
        return user.getFriends()
                .stream()
                .map(this::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        List<User> userFriends = getAllFriends(userId);
        List<User> otherUserFriends = getAllFriends(otherId);
        List<User> commonFriends = new ArrayList<>(userFriends);
        commonFriends.retainAll(otherUserFriends);
        return commonFriends;
    }
    private void validateUser(User user) throws ValidationException {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.info("Login used as username");
        }
        if (user.getId() != 0) {
            log.warn("Request body has id field");
            throw new ValidationException("Bad request");
        }
        user.setId(userId++);
        log.info("Validation successful");
    }
}
