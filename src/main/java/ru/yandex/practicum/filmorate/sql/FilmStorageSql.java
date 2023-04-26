package ru.yandex.practicum.filmorate.sql;

public enum FilmStorageSql {
    FIND_BY_REQUESTED_DIRECTOR(
            "SELECT f.ID, f.NAME, f.DESCRIPTION, f.DURATION, f.RELEASE_DATE, f.MPA_ID, m.NAME AS MPA_NAME, " +
                    "(SELECT COUNT(l.USER_ID) FROM LIKES l WHERE f.ID = l.FILM_ID) countLikes " +
                    "FROM FILM f " +
                    "JOIN MPA m ON f.MPA_ID = m.MPA_ID " +
                    "JOIN FILM_DIRECTOR fd ON f.ID = fd.FILM_ID " +
                    "JOIN DIRECTOR d ON d.DIRECTOR_ID = fd.DIRECTOR_ID " +
                    "WHERE LCASE(d.DIRECTOR_NAME) LIKE CONCAT('%', ?, '%') " +
                    "ORDER BY countLikes DESC"),
    FIND_BY_REQUESTED_TITLE(
            "SELECT f.ID, f.NAME, f.DESCRIPTION, f.DURATION, f.RELEASE_DATE, f.MPA_ID, m.NAME AS MPA_NAME, " +
                    "(SELECT COUNT(l.USER_ID) FROM LIKES l WHERE f.ID = l.FILM_ID) countLikes " +
                    "FROM FILM f " +
                    "JOIN MPA m ON f.MPA_ID = m.MPA_ID " +
                    "WHERE LCASE(f.NAME) LIKE CONCAT('%', ?, '%') " +
                    "ORDER BY countLikes DESC"),
    FIND_BY_REQUESTED_TITLE_AND_DIRECTOR(
            "SELECT f.ID, f.NAME, f.DESCRIPTION, f.DURATION, f.RELEASE_DATE, f.MPA_ID, m.NAME AS MPA_NAME, " +
                    "(SELECT COUNT(l.USER_ID) FROM LIKES l WHERE f.ID = l.FILM_ID) countLikes " +
                    "FROM FILM f " +
                    "JOIN MPA m ON f.MPA_ID = m.MPA_ID " +
                    "LEFT JOIN FILM_DIRECTOR fd ON f.ID = fd.FILM_ID " +
                    "LEFT JOIN DIRECTOR d ON d.DIRECTOR_ID = fd.DIRECTOR_ID " +
                    "WHERE LCASE(d.DIRECTOR_NAME) LIKE CONCAT('%', ?, '%') " +
                    "OR LCASE(f.NAME) LIKE CONCAT('%', ?, '%') " +
                    "ORDER BY countLikes DESC");

    private final String sql;

    FilmStorageSql(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }
}