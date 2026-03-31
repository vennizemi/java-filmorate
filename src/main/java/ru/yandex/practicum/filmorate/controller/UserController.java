package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("GET запрос: получение всех пользователей");

        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {

        // формируем дополнительные данные
        log.debug("POST запрос: создание пользователя с email='{}', login='{}'",
                        user.getEmail(), user.getLogin());

        user.setId(getNextId());
        user.setEmail(user.getEmail());

        user.setLogin(user.getLogin());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }else {
            user.setName(user.getName());
        }
        user.setBirthday(user.getBirthday());

        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@Validated(OnUpdate.class) @RequestBody User newUser) {
        log.debug("PUT запрос: обновление пользователя с id={}", newUser);

        if (users.containsKey(newUser.getId())) {

            User oldUser = users.get(newUser.getId());
            //если публикация найдена и все условия соблюдены, обновляем её содержимое

            oldUser.setEmail(newUser.getEmail());

            oldUser.setLogin(newUser.getLogin());
            if (newUser.getName() == null || newUser.getName().isBlank()) {
                oldUser.setName(newUser.getLogin());
            }else {
                oldUser.setName(newUser.getName());
            }
            oldUser.setBirthday(newUser.getBirthday());

            return oldUser;
        }
        else {
            throw new NotFoundException("User с id = " + newUser.getId() + " не найден");
        }
    }

    //вспомогательный метод для генерации нового поста
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
