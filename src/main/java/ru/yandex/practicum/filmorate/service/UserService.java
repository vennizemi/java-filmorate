package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        log.info("Запрос на получение всех пользователей");
        return userStorage.findAll();
    }

    public User findById(long id) {
        log.info("Запрос на получение пользователя с id={}", id);
        User user = userStorage.findById(id);
        if (user == null) {
            log.warn("Пользователь с id {} не найден", id);
            throw new IllegalArgumentException("Пользователь с id " + id + " не найден");
        }
        return user;
    }

    public User create(User user) {
        log.info("Создание пользователя с email='{}', login='{}'",
                user.getEmail(), user.getLogin());
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User addFriend(long userId, long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);

        if (user == null || friend == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        log.info("Добавление в друзья: пользователь {} -> {}", userId, friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        log.info("Пользователи {} и {} теперь друзья", userId, friendId);
        return user;
    }

    public User removeFriend(long userId, long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);

        if (user == null || friend == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        return user;
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        User user = userStorage.findById(userId);
        User other = userStorage.findById(otherId);

        if (user == null || other == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        Set<Long> userFriends = user.getFriends();
        Set<Long> otherFriends = other.getFriends();

        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(userStorage::findById)
                .toList();
    }

    public Collection<User> getFriends(long id) {
        User user = userStorage.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь с id " + id + " не найден");
        }
        return user.getFriends().stream()
                .map(userStorage::findById)
                .toList();
    }
}