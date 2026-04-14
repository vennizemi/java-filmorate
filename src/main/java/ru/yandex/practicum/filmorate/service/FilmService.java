package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
public class FilmService {
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> findAll() {
        log.info("Запрос на получение всех фильмов");
        return filmStorage.findAll();
    }

    public Film findById(long id) {
        log.info("Запрос на получение фильма с id={}", id);
        Film film = filmStorage.findById(id);
        if (film == null) {
            log.warn("Фильм с id {} не найден", id);
            throw new IllegalArgumentException("Фильм с id " + id + " не найден");
        }
        return film;
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film addLike(long filmId, long userId) {
        log.info("Пользователь {} ставит лайк фильму {}", userId, filmId);
        Film film = filmStorage.findById(filmId);
        if (film == null) {
            throw new IllegalArgumentException("Фильм не найден");
        }

        log.info("Лайк от пользователя {} добавлен к фильму {}", userId, filmId);
        film.getLikes().add(userId);
        return film;
    }

    public Film removeLike(long filmId, long userId) {
        Film film = filmStorage.findById(filmId);
        if (film == null) {
            throw new IllegalArgumentException("Фильм не найден");
        }

        film.getLikes().remove(userId);
        return film;
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparing(film -> -film.getLikes().size()))
                .limit(count)
                .toList();
    }
}