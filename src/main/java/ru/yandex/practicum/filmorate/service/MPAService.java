package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.util.Collection;

@Service
public class MPAService {
    private static final Logger log = LoggerFactory.getLogger(MPAService.class);
    private final MPAStorage mpaStorage;

    @Autowired
    public MPAService(MPAStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Collection<MPA> findAll() {
        log.info("Запрос на получение всех рейтингов MPA");
        return mpaStorage.findAll();
    }

    public MPA findById(int id) {
        log.info("Запрос на получение рейтинга MPA с id={}", id);
        MPA mpa = mpaStorage.findById(id);
        if (mpa == null) {
            log.warn("Рейтинг MPA с id {} не найден", id);
            throw new IllegalArgumentException("Рейтинг MPA с id " + id + " не найден");
        }
        return mpa;
    }
}