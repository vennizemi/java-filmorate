package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTests {
    private Film validFilm;

    @BeforeEach
    void setUp() {
        validFilm = new Film();
        validFilm.setId(1L);
        validFilm.setName("Властелин колец");
        validFilm.setDescription("Эпическая фэнтези-сага");
        validFilm.setReleaseDate(LocalDate.of(1995, 12, 28));
        validFilm.setDuration(178);
    }

    @Test
    void shouldOnlyAcceptValidName() {
        // Граничное условие: имя пустое
        validFilm.setName("");
        assertFalse(FilmValidator.isValid(validFilm));

        validFilm.setName(null);
        assertFalse(FilmValidator.isValid(validFilm));

        validFilm.setName("a");
        assertTrue(FilmValidator.isValid(validFilm));
    }

    @Test
    void shouldOnlyAcceptValidDescriptionLength() {
        // Граничное условие: ровно 200 символов — ок, 201 — нет
        validFilm.setDescription("A".repeat(200));
        assertTrue(FilmValidator.isValid(validFilm));

        validFilm.setDescription("A".repeat(201));
        assertFalse(FilmValidator.isValid(validFilm));
    }

    @Test
    void shouldOnlyAcceptValidReleaseDate() {
        // Граничное условие: 28.12.1895 — минимальная дата
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertTrue(FilmValidator.isValid(validFilm));

        validFilm.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertFalse(FilmValidator.isValid(validFilm));
    }

    @Test
    void shouldOnlyAcceptPositiveDuration() {
        // Граничное условие: duration > 0
        validFilm.setDuration(1);
        assertTrue(FilmValidator.isValid(validFilm));

        validFilm.setDuration(0);
        assertFalse(FilmValidator.isValid(validFilm));

        validFilm.setDuration(-1);
        assertFalse(FilmValidator.isValid(validFilm));
    }

    @Test
    void shouldRejectFilmWithWhitespaceOnlyName() {
        // Имя состоит только из пробелов
        validFilm.setName("   ");
        assertFalse(FilmValidator.isValid(validFilm));
    }

    @Test
    void shouldRejectFilmWithNullReleaseDate() {
        // Дата релиза не указана
        validFilm.setReleaseDate(null);
        assertFalse(FilmValidator.isValid(validFilm));
    }

    @Test
    void shouldRejectFilmWithNullDuration() {
        // Продолжительность не указана
        validFilm.setDuration(null);
        assertFalse(FilmValidator.isValid(validFilm));
    }
}
