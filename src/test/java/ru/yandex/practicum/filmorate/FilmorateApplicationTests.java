package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
class FilmorateApplicationTests {
	@Autowired
	private FilmController filmController;
	@Autowired
	private UserController userController;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void contextLoads() {
		assertThat(filmController).isNotNull();
		assertThat(userController).isNotNull();
	}

	@SneakyThrows
	@Test
	void testValidationFilms() {
		String validFilm = objectMapper.writeValueAsString(Film.builder()
						.name("nisi eiusmod")
						.description("adipisicing")
						.releaseDate(LocalDate.of(1967, 3,25))
						.duration(100)
						.build());
		String inValidFilm =
				objectMapper.writeValueAsString(Film.builder()
						.name("nisi eiusmod")
						.description("adipisicing")
						.releaseDate(LocalDate.of(1967, 3,25))
						.duration(-100) // указал отрицательное значение
						.build());

		mockMvc.perform(post("/films")
				.contentType("application/json")
				.content(validFilm)).andDo(
				h-> {
					assertEquals(200, h.getResponse().getStatus());
				}
		);
		mockMvc.perform(get("/films")
				.contentType("application/json")).andDo(h->
				assertEquals(200, h.getResponse().getStatus())
		);
		mockMvc.perform(post("/films")
				.contentType("application/json")
				.content(inValidFilm)).andDo(
				h-> {
					assertEquals(400, h.getResponse().getStatus());
				}
		);
	}

	@SneakyThrows
	@Test
	void testValidationUsers() {
		String validUser = objectMapper.writeValueAsString(User.builder()
						.login("dolore")
						.name("Nick Name")
						.email("mail@mail.ru")
						.birthday(LocalDate.of(1946,8,20))
						.build());
		String inValidUser = objectMapper.writeValueAsString(User.builder()
						.login("dolore")
						.name("Nick Name")
						.email("mail.ru") // неверный формат
						.birthday(LocalDate.of(1980,8,20))
						.build());

		mockMvc.perform(post("/users")
				.contentType("application/json")
				.content(validUser)).andDo(
				h-> {
					assertEquals(200, h.getResponse().getStatus());
				}
		);
		mockMvc.perform(get("/users")
				.contentType("application/json")).andDo(h->
				assertEquals(200, h.getResponse().getStatus())
		);
		mockMvc.perform(post("/users")
				.contentType("application/json")
				.content(inValidUser)).andDo(
				h-> {
					assertEquals(400, h.getResponse().getStatus());
				}
		);
	}
}
