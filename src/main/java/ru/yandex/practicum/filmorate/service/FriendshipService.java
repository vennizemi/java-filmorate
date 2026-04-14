package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendshipService {

    private final FriendshipStorage friendshipStorage;
    private final UserStorage userStorage;

    public void addFriend(Long userId, Long friendId) {
        validateUsersExist(userId, friendId);
        
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("User cannot add themselves as a friend");
        }

        Friendship friendship = new Friendship(userId, friendId);
        
        if (friendshipStorage.exists(userId, friendId)) {
            FriendshipStatus status = friendshipStorage.getStatus(userId, friendId);
            if (status == FriendshipStatus.CONFIRMED) {
                log.info("Users {} and {} are already friends", userId, friendId);
                return;
            }
            // If request exists but not confirmed, we can update it
        }
        
        friendshipStorage.addFriend(friendship);
        log.info("Friend request sent from {} to {}", userId, friendId);
    }

    public void confirmFriendship(Long userId, Long friendId) {
        validateUsersExist(userId, friendId);
        
        if (!friendshipStorage.exists(friendId, userId)) {
            throw new IllegalArgumentException("No friend request from " + friendId + " to " + userId);
        }
        
        friendshipStorage.confirmFriendship(userId, friendId);
        log.info("Friendship confirmed between {} and {}", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        validateUsersExist(userId, friendId);
        
        friendshipStorage.removeFriend(userId, friendId);
        // Also remove reverse relation if it exists
        if (friendshipStorage.exists(friendId, userId)) {
            friendshipStorage.removeFriend(friendId, userId);
        }
        log.info("Friend relation removed between {} and {}", userId, friendId);
    }

    public Collection<User> getUserFriends(Long userId) {
        userStorage.findById(userId); // Validate user exists
        return friendshipStorage.getUserFriends(userId).stream()
                .map(userStorage::findById)
                .toList();
    }

    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        userStorage.findById(userId); // Validate users exist
        userStorage.findById(otherId);
        return friendshipStorage.getCommonFriends(userId, otherId).stream()
                .map(userStorage::findById)
                .toList();
    }

    private void validateUsersExist(Long userId, Long friendId) {
        try {
            userStorage.findById(userId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("User with id " + userId + " not found");
        }
        
        try {
            userStorage.findById(friendId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("User with id " + friendId + " not found");
        }
    }
}
