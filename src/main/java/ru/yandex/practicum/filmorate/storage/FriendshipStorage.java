package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.Collection;

public interface FriendshipStorage {
    void addFriend(Friendship friendship);
    void removeFriend(Long userId, Long friendId);
    Collection<Long> getUserFriends(Long userId);
    Collection<Long> getCommonFriends(Long userId, Long otherId);
}
