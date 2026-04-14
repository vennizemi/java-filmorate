package ru.yandex.practicum.filmorate.storage.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@Sql(scripts = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UserDbStorageTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private UserDbStorage userStorage;

    private User testUser;

    @BeforeEach
    public void setUp() {
        userStorage = new UserDbStorage(jdbcTemplate);
        testUser = User.builder()
                .email("test@example.com")
                .login("testuser")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
    }

    @Test
    public void testCreateUser() {
        User createdUser = userStorage.create(testUser);

        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser).hasFieldOrPropertyWithValue("email", "test@example.com");
        assertThat(createdUser).hasFieldOrPropertyWithValue("login", "testuser");
        assertThat(createdUser).hasFieldOrPropertyWithValue("name", "Test User");
        assertThat(createdUser).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1990, 1, 1));

        User foundUser = userStorage.findById(createdUser.getId());
        assertThat(foundUser).isEqualTo(createdUser);
    }

    @Test
    public void testFindById() {
        User createdUser = userStorage.create(testUser);

        User foundUser = userStorage.findById(createdUser.getId());

        assertThat(foundUser).isEqualTo(createdUser);
    }

    @Test
    public void testFindByIdNotFound() {
        assertThatThrownBy(() -> userStorage.findById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    public void testUpdateUser() {
        User createdUser = userStorage.create(testUser);

        createdUser.setName("Updated Name");
        createdUser.setEmail("updated@example.com");

        User updatedUser = userStorage.update(createdUser);

        assertThat(updatedUser).hasFieldOrPropertyWithValue("name", "Updated Name");
        assertThat(updatedUser).hasFieldOrPropertyWithValue("email", "updated@example.com");

        User foundUser = userStorage.findById(createdUser.getId());
        assertThat(foundUser).isEqualTo(updatedUser);
    }

    @Test
    public void testUpdateUserNotFound() {
        User user = User.builder()
                .id(999L)
                .email("test@example.com")
                .login("testuser")
                .build();

        assertThatThrownBy(() -> userStorage.update(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    public void testFindAll() {
        User user1 = userStorage.create(User.builder()
                .email("user1@example.com")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(1990, 1, 1))
                .build());

        User user2 = userStorage.create(User.builder()
                .email("user2@example.com")
                .login("user2")
                .name("User Two")
                .birthday(LocalDate.of(1995, 5, 5))
                .build());

        Collection<User> users = userStorage.findAll();

        assertThat(users).hasSize(2);
        assertThat(users).containsExactlyInAnyOrder(user1, user2);
    }
}
