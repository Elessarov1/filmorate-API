package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.recommendations.RecommendationStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final RecommendationStorage recStorage;

    public List<Film> getRecommendations(int userId) {
        int filmCount = 3;
        User targetUser = userStorage.get(userId);
        List<User> users = new ArrayList<>(userStorage.getAllUsers());
        users.remove(targetUser);
        List<User> mostSimilarUsers = getMostSimilarUsers(targetUser, users);

        return getRecommendedFilms(targetUser, mostSimilarUsers, filmCount);
    }

    private int calcUserSimilarities(User targetUser, User otherUser) {
        Set<Integer> commonLikes = new HashSet<>(recStorage.getUserLikes(targetUser.getId()));
        commonLikes.retainAll(recStorage.getUserLikes(otherUser.getId()));

        return commonLikes.size();
    }

    private List<User> getMostSimilarUsers(User targetUser,
                                           List<User> allUsers) {
        List<User> sortedUsers = new ArrayList<>(allUsers);
        sortedUsers.sort((u1,u2) -> Double.compare(
                calcUserSimilarities(targetUser, u2),
                calcUserSimilarities(targetUser, u1)));
        return sortedUsers;
    }

    private List<Film> getRecommendedFilms(User targetuser,
                                        List<User> mostSimilarUsers,
                                        int count) {
        Map<Film, Integer> filmsLiked = new HashMap<>();
        Set<Integer> targetUserLikes = new HashSet<>(recStorage.getUserLikes(targetuser.getId()));

        for (User user : mostSimilarUsers) {
            for (Integer filmId : recStorage.getUserLikes(user.getId())) {
                if (!targetUserLikes.contains(filmId)) {
                    Film film = filmStorage.get(filmId);
                    filmsLiked.put(film, filmsLiked.getOrDefault(film, 0) + 1);
                }
            }
        }

        return filmsLiked.keySet().stream()
                .sorted((f1, f2) -> Integer.compare(filmsLiked.get(f2), filmsLiked.get(f1)))
                .limit(count)
                .collect(Collectors.toList());
    }
}
