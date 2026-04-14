package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FriendshipDbStorage implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(Friendship friendship) {
        log.info("Adding friend relation: {} -> {}", friendship.getUserId(), friendship.getFriendId());
        String sql = "INSERT INTO user_friends (user_id, friend_id, status) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, friendship.getUserId(), friendship.getFriendId(), friendship.getStatus().name());
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        log.info("Removing friend relation: {} -> {}", userId, friendId);
        String sql = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public Collection<Long> getUserFriends(Long userId) {
        String sql = "SELECT friend_id FROM user_friends WHERE user_id = ? AND status = 'CONFIRMED'";
        return jdbcTemplate.queryForList(sql, Long.class, userId);
    }

    @Override
    public Collection<Long> getCommonFriends(Long userId, Long otherId) {
        String sql = "SELECT uf1.friend_id FROM user_friends uf1 " +
                "INNER JOIN user_friends uf2 ON uf1.friend_id = uf2.friend_id " +
                "WHERE uf1.user_id = ? AND uf2.user_id = ? " +
                "AND uf1.status = 'CONFIRMED' AND uf2.status = 'CONFIRMED'";
        return jdbcTemplate.queryForList(sql, Long.class, userId, otherId);
    }

    public void confirmFriendship(Long userId, Long friendId) {
        log.info("Confirming friend relation: {} <- {}", userId, friendId);
        String sql = "UPDATE user_friends SET status = 'CONFIRMED' WHERE user_id = ? AND friend_id = ?";
        int updated = jdbcTemplate.update(sql, userId, friendId);
        if (updated == 0) {
            throw new IllegalArgumentException("Friendship not found");
        }
    }

    public boolean exists(Long userId, Long friendId) {
        String sql = "SELECT COUNT(*) FROM user_friends WHERE user_id = ? AND friend_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, friendId);
        return count != null && count > 0;
    }

    public FriendshipStatus getStatus(Long userId, Long friendId) {
        try {
            String sql = "SELECT status FROM user_friends WHERE user_id = ? AND friend_id = ?";
            String status = jdbcTemplate.queryForObject(sql, String.class, userId, friendId);
            return FriendshipStatus.valueOf(status);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
