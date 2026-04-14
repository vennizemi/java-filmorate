package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

@Service
public class GenreService {
    private static final Logger log = LoggerFactory.getLogger(GenreService.class);
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Collection<Genre> findAll() {
        log.info("Запрос на получение всех жанров");
        return genreStorage.findAll();
    }

    public Genre findById(int id) {
        log.info("Запрос на получение жанра с id={}", id);
        Genre genre = genreStorage.findById(id);
        if (genre == null) {
            log.warn("Жанр с id {} не найден", id);
            throw new IllegalArgumentException("Жанр с id " + id + " не найден");
        }
        return genre;
    }
}