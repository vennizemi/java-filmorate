package ru.yandex.practicum.filmorate.model;

/**
 * Статус дружбы между пользователями.
 */
public enum FriendshipStatus {
    /**
     * Запрос на дружбу отправлен, но ещё не подтверждён.
     */
    PENDING,

    /**
     * Дружба подтверждена обоими пользователями.
     */
    CONFIRMED
}
