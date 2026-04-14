package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.util.Collection;

public interface FriendshipStorage {
    void addFriend(Friendship friendship);
    void removeFriend(Long userId, Long friendId);
    boolean exists(Long userId, Long friendId);
    void confirmFriendship(Long userId, Long friendId);
    FriendshipStatus getStatus(Long userId, Long friendId);
    Collection<Long> getUserFriends(Long userId);
    Collection<Long> getCommonFriends(Long userId, Long otherId);
}
