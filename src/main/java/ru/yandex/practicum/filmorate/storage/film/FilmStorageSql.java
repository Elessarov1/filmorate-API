package ru.yandex.practicum.filmorate.storage.film;

public class FilmStorageSql {
    final static String OR = "OR";
    final static String AND = "AND";
    final static String WHERE = "WHERE";
    final static String LIMIT = "LIMIT ?";
    final static String GENRE_ID = "fg.GENRE_ID = ?";
    final static String YEAR = "YEAR(RELEASE_DATE) = ?";
    final static String LEFT = "LEFT %s";
    final static String FILM_NAME = "f.NAME";
    final static String DIRECTOR_NAME = "d.DIRECTOR_NAME";
    final static String GET_ALL_FILMS = "SELECT f.ID, f.NAME, f.DESCRIPTION, f.DURATION, f.RELEASE_DATE, f.MPA_ID, m.NAME AS MPA_NAME, " +
            "(SELECT COUNT(l.USER_ID) FROM LIKES l WHERE f.ID = l.FILM_ID) countLikes " +
            "FROM FILM f " +
            "JOIN MPA m ON f.MPA_ID = m.MPA_ID ";
    final static String JOIN_FILM_DIRECTOR = "JOIN FILM_DIRECTOR fd ON f.ID = fd.FILM_ID";
    final static String JOIN_DIRECTOR = "JOIN DIRECTOR d ON d.DIRECTOR_ID = fd.DIRECTOR_ID";
    final static String JOIN_FILM_GENRE = "JOIN FILM_GENRE fg ON f.ID = fg.FILM_ID";
    final static String JOIN_GENRE = "JOIN GENRE g ON g.GENRE_ID = fg.GENRE_ID";
    final static String LCASE = "LCASE(%s) LIKE CONCAT('%%', ?, '%%')";
    final static String GROUP_BY_ID = "GROUP BY f.ID";
    final static String ORDER_BY_COUNT_LIKES_DESC = "ORDER BY countLikes DESC";
}