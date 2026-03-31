package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("GET запрос: получение всех фильмов");

        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {

        // формируем дополнительные данные
        log.debug("POST запрос: создание фильма {}", film);

        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@Validated(OnUpdate.class) @RequestBody Film newFilm) {

        if (films.containsKey(newFilm.getId())) {

            log.debug("PUT запрос: обновление фильма {}", newFilm);

            Film oldFilm = films.get(newFilm.getId());
            //если публикация найдена и все условия соблюдены, обновляем её содержимое
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());

            return oldFilm;
        }
        else {
            throw new NotFoundException("Film с id = " + newFilm.getId() + " не найден");
        }
    }

    //вспомогательный метод для генерации нового поста
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }



}
