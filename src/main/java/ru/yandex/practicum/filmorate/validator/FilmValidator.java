package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmValidator {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public static boolean isValid(Film film) {
        if (film == null) return false;
        if (film.getName() == null || film.getName().trim().isEmpty()) return false;
        if (film.getDescription() != null && film.getDescription().length() > 200) return false;
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) return false;
        if (film.getDuration() == null || film.getDuration() <= 0) return false;

        return true;
    }
}