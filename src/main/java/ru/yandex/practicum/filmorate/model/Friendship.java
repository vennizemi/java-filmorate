package ru.yandex.practicum.filmorate.model;

import lombok.Data;

/**
 * Сущность дружбы между двумя пользователями.
 * Используется для представления связи "пользователь - друг" с указанием статуса.
 */
@Data
public class Friendship {
    private Long userId;
    private Long friendId;
    private FriendshipStatus status;

    public Friendship(Long userId, Long friendId) {
        this.userId = userId;
        this.friendId = friendId;
        this.status = FriendshipStatus.PENDING;
    }
}