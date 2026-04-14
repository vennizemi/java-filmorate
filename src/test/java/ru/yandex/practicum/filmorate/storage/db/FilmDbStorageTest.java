package ru.yandex.practicum.filmorate.storage.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@Sql(scripts = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class FilmDbStorageTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private FilmDbStorage filmStorage;

    private Film testFilm;

    private MPA testMPA;

    @BeforeEach
    public void setUp() {
        filmStorage = new FilmDbStorage(jdbcTemplate);
        testMPA = MPA.builder()
                .id(1)
                .name("G")
                .build();
        testFilm = Film.builder()
                .name("Test Film")
                .description("Test description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .mpa(testMPA)
                .build();
    }

    @Test
    public void testCreateFilm() {
        // Создаем фильм
        Film createdFilm = filmStorage.create(testFilm);

        // Проверяем, что фильм создан с правильными данными
        assertThat(createdFilm.getId()).isNotNull();
        assertThat(createdFilm).hasFieldOrPropertyWithValue("name", "Test Film");
        assertThat(createdFilm).hasFieldOrPropertyWithValue("description", "Test description");
        assertThat(createdFilm).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2020, 1, 1));
        assertThat(createdFilm).hasFieldOrPropertyWithValue("duration", 120);
        assertThat(createdFilm.getMpa()).hasFieldOrPropertyWithValue("id", 1);
        assertThat(createdFilm.getMpa()).hasFieldOrPropertyWithValue("name", "G");

        // Проверяем, что фильм действительно сохранен в базе
        Film foundFilm = filmStorage.findById(createdFilm.getId());
        assertThat(foundFilm).isEqualTo(createdFilm);
    }

    @Test
    public void testFindById() {
        // Создаем фильм
        Film createdFilm = filmStorage.create(testFilm);

        // Ищем фильм по ID
        Film foundFilm = filmStorage.findById(createdFilm.getId());

        // Проверяем, что найденный фильм соответствует ожидаемому
        assertThat(foundFilm).isEqualTo(createdFilm);
    }

    @Test
    public void testFindByIdNotFound() {
        // Пытаемся найти несуществующий фильм
        assertThatThrownBy(() -> filmStorage.findById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    public void testUpdateFilm() {
        // Создаем фильм
        Film createdFilm = filmStorage.create(testFilm);

        // Обновляем данные фильма
        createdFilm.setName("Updated Name");
        createdFilm.setDescription("Updated description");
        createdFilm.setDuration(150);

        Film updatedFilm = filmStorage.update(createdFilm);

        // Проверяем, что фильм обновлен
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("name", "Updated Name");
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("description", "Updated description");
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("duration", 150);

        // Проверяем, что изменения сохранены в базе
        Film foundFilm = filmStorage.findById(createdFilm.getId());
        assertThat(foundFilm).isEqualTo(updatedFilm);
    }

    @Test
    public void testUpdateFilmNotFound() {
        // Пытаемся обновить несуществующий фильм
        Film film = Film.builder()
                .id(999L)
                .name("Test Film")
                .build();

        assertThatThrownBy(() -> filmStorage.update(film))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    public void testFindAll() {
        // Создаем несколько фильмов
        Film film1 = filmStorage.create(Film.builder()
                .name("Film One")
                .description("Description One")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .mpa(testMPA)
                .build());

        Film film2 = filmStorage.create(Film.builder()
                .name("Film Two")
                .description("Description Two")
                .releaseDate(LocalDate.of(2021, 1, 1))
                .duration(130)
                .mpa(testMPA)
                .build());

        // Получаем все фильмы
        Collection<Film> films = filmStorage.findAll();

        // Проверяем, что найдены все фильмы
        assertThat(films).hasSize(2);
        assertThat(films).containsExactlyInAnyOrder(film1, film2);
    }
}