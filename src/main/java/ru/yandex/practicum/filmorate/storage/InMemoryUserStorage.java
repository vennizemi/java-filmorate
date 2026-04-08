package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        log.info("GET запрос: получение всех пользователей");
        return users.values();
    }

    @Override
    public User create(User user) {
        log.debug("POST запрос: создание пользователя с email='{}', login='{}'",
                user.getEmail(), user.getLogin());

        user.setId(getNextId());
        user.setEmail(user.getEmail());
        user.setLogin(user.getLogin());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        } else {
            user.setName(user.getName());
        }
        user.setBirthday(user.getBirthday());

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        log.debug("PUT запрос: обновление пользователя с id={}", user.getId());

        if (users.containsKey(user.getId())) {
            User oldUser = users.get(user.getId());
            oldUser.setEmail(user.getEmail());
            oldUser.setLogin(user.getLogin());
            if (user.getName() == null || user.getName().isBlank()) {
                oldUser.setName(user.getLogin());
            } else {
                oldUser.setName(user.getName());
            }
            oldUser.setBirthday(user.getBirthday());
            return oldUser;
        } else {
            throw new NotFoundException("User с id = " + user.getId() + " не найден");
        }
    }

    @Override
    public User findById(Long id) {
        return users.get(id);
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
