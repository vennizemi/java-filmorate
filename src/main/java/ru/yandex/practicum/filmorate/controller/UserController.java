package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable @Positive long id) {
        return userService.findById(id);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User update(@Validated(OnUpdate.class) @RequestBody User user) {
        return userService.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable @Positive long id, @PathVariable @Positive long friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User removeFriend(@PathVariable long id, @PathVariable long friendId) {
        return userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable long id) {
        return userService.getFriends(id);
    }
}