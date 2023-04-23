package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    private static User user1;
    private static User user2;
    private static Film film1;
    private static Film film2;
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final GenreDbStorage genreStorage;
    private final MpaDbStorage mpaStorage;

    @BeforeAll
    static void beforeAll() {
        user1 = User.builder()
                .id(1)
                .name("Tom")
                .login("tom")
                .email("example@gmail.com")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

        user2 = User.builder()
                .id(2)
                .name("Dan")
                .login("dan")
                .email("example@gmail.com")
                .birthday(LocalDate.of(1987, 3, 15))
                .build();

        film1 = Film.builder()
                .id(1)
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .mpa(new Mpa(1, "G"))
                .build();

        film2 = Film.builder()
                .id(2)
                .name("Titanic")
                .description("description")
                .releaseDate(LocalDate.of(2000, 5, 20))
                .duration(120)
                .mpa(new Mpa(2, "PG"))
                .build();
    }

    @BeforeEach
    void setUp() {
        userStorage.add(user1);
        userStorage.add(user2);
        filmStorage.add(film1);
        filmStorage.add(film2);
    }

    @AfterEach
    void tearDown() {
        userStorage.delete(user1.getId());
        userStorage.delete(user2.getId());
        filmStorage.delete(film1.getId());
        filmStorage.delete(film2.getId());
    }

    @Test
    void getUserById() {
        assertEquals(user1, userStorage.get(user1.getId()));
    }

    @Test
    void getAllUsers() {
        List<User> users = userStorage.getAllUsers();
        assertEquals(user1, users.get(0));
        assertEquals(user2, users.get(1));
        assertEquals(2, users.size());
    }

    @Test
    void updateUser() {
        User updatedUser = User.builder()
                .id(user1.getId())
                .login(user1.getLogin())
                .name("otherName")
                .email(user1.getEmail())
                .birthday(user1.getBirthday())
                .build();
        userStorage.update(updatedUser);
        assertEquals(updatedUser, userStorage.get(updatedUser.getId()));
    }

    @Test
    void deleteUser() {
        assertTrue(userStorage.delete(user1.getId()));
        assertThrows(NotFoundException.class, () -> userStorage.get(user1.getId()));
    }

    @Test
    void addToFriends() {
        assertTrue(userStorage.addFriend(user1.getId(), user2.getId()));
        assertTrue(userStorage.deleteFriend(user1.getId(), user2.getId()));
        assertFalse(userStorage.deleteFriend(user1.getId(), user2.getId()));
    }

    @Test
    void getFilmById() {
        assertEquals(film1, filmStorage.get(film1.getId()));
    }

    @Test
    void getAllFilms() {
        List<Film> films = filmStorage.getAllFilms();
        assertEquals(film1, films.get(0));
        assertEquals(film2, films.get(1));
        assertEquals(2, films.size());
    }

    @Test
    void updateFilm() {
        Film updatedFilm = Film.builder()
                .id(film1.getId())
                .name("Other Name")
                .description(film1.getDescription())
                .releaseDate(film1.getReleaseDate())
                .duration(film1.getDuration())
                .mpa(film1.getMpa())
                .build();
        filmStorage.update(updatedFilm);
        assertEquals(updatedFilm, filmStorage.get(film1.getId()));
    }

    @Test
    void deleteFilm() {
        assertTrue(filmStorage.delete(film1.getId()));
        assertThrows(NotFoundException.class, () -> filmStorage.get(film1.getId()));
    }

    @Test
    void getMpaById() {
        Mpa expectedMpa = Mpa.builder()
                .id(1)
                .name("G")
                .build();
        assertEquals(expectedMpa, mpaStorage.get(1));
    }

    @Test
    void getAllMpa() {
        List<Mpa> mpaList = mpaStorage.getAllMpa();
        assertEquals(5, mpaList.size());
    }

    @Test
    void getGenreById() {
        Genre expectedGenre = Genre.builder()
                .id(1)
                .name("Комедия")
                .build();
        assertEquals(expectedGenre, genreStorage.get(1));
    }

    @Test
    void getAllGenre() {
        List<Genre> genres = genreStorage.getAllGenres();
        assertEquals(6, genres.size());
    }
}

