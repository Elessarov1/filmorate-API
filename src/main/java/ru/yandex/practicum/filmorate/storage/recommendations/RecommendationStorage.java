package ru.yandex.practicum.filmorate.storage.recommendations;

import java.util.List;

public interface RecommendationStorage {
    List<Integer> getUserLikes(int id);
}
