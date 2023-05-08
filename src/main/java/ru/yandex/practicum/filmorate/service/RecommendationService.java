package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.recommendations.RecommendationStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    private final RecommendationStorage recStorage;

    public List<Film> getRecommendations(int userId) {
        User targetUser = userStorage.get(userId);
        List<User> users = new ArrayList<>(userStorage.getAllUsers());
        List<User> mostSimilarUsers = getMostSimilarUsers(targetUser, users);

        return getRecommendedFilms(targetUser, mostSimilarUsers);
    }

    private int calcUserSimilarities(User targetUser, User otherUser) {
        Set<Integer> commonLikes = new HashSet<>(recStorage.getUserLikes(targetUser.getId()));
        commonLikes.retainAll(recStorage.getUserLikes(otherUser.getId()));

        return commonLikes.size();
    }

    private List<User> getMostSimilarUsers(User targetUser, List<User> allUsers) {
        List<User> sortedUsers = new ArrayList<>(allUsers);
        if (allUsers.size() == 1) {
            return sortedUsers;
        }
        sortedUsers.sort((u1,u2) -> Integer.compare(
                calcUserSimilarities(targetUser, u2),
                calcUserSimilarities(targetUser, u1)));
        return sortedUsers;
    }

    private List<Film> getRecommendedFilms(User targetuser, List<User> mostSimilarUsers) {
        List<Film> recommendedFilms = new ArrayList<>();
        Set<Integer> targetUserLikes = new HashSet<>(recStorage.getUserLikes(targetuser.getId()));

        for (User user : mostSimilarUsers) {
            for (Integer filmId : recStorage.getUserLikes(user.getId())) {
                if (!targetUserLikes.contains(filmId)) {
                    Film film = filmStorage.get(filmId);
                    recommendedFilms.add(film);
                }
            }
        }

        return recommendedFilms.stream()
                .sorted(Comparator.comparing(Film::getLikesCount).reversed())
                .collect(Collectors.toList());
    }
}
