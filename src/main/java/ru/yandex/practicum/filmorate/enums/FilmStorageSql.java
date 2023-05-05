package ru.yandex.practicum.filmorate.enums;

public enum FilmStorageSql {
    GET_BY_GENRE_AND_YEAR(
            "SELECT f.ID, f.NAME, f.DESCRIPTION, f.DURATION, f.RELEASE_DATE, f.MPA_ID, m.NAME AS MPA_NAME, " +
                    "(SELECT COUNT(l.USER_ID) FROM LIKES l WHERE f.ID = l.FILM_ID) countLikes " +
                    "FROM FILM f " +
                    "JOIN MPA m ON f.MPA_ID = m.MPA_ID " +
                    "JOIN FILM_GENRE fg ON f.ID = fg.FILM_ID " +
                    "JOIN GENRE g ON g.GENRE_ID = fg.GENRE_ID " +
                    "WHERE fg.GENRE_ID = ? " +
                    "AND YEAR(RELEASE_DATE) = ? " +
                    "GROUP BY f.ID " +
                    "ORDER BY countLikes DESC " +
                    "LIMIT ?"),
    GET_BY_GENRE_OR_YEAR(
            "SELECT f.ID, f.NAME, f.DESCRIPTION, f.DURATION, f.RELEASE_DATE, f.MPA_ID, m.NAME AS MPA_NAME, " +
                    "(SELECT COUNT(l.USER_ID) FROM LIKES l WHERE f.ID = l.FILM_ID) countLikes " +
                    "FROM FILM f " +
                    "JOIN MPA m ON f.MPA_ID = m.MPA_ID " +
                    "JOIN FILM_GENRE fg ON f.ID = fg.FILM_ID " +
                    "JOIN GENRE g ON g.GENRE_ID = fg.GENRE_ID " +
                    "WHERE fg.GENRE_ID = ? " +
                    "OR YEAR(RELEASE_DATE) = ? " +
                    "GROUP BY f.ID " +
                    "ORDER BY countLikes DESC " +
                    "LIMIT ?");

    private final String sql;

    FilmStorageSql(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }
}