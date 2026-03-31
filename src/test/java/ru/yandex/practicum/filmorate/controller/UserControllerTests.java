package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserControllerTests {
    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User();
        validUser.setId(1L);
        validUser.setEmail("user@example.com");
        validUser.setLogin("validlogin");
        validUser.setName("Иван Иванов");
        validUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void shouldOnlyAcceptValidEmail() {

        // Корректный email
        validUser.setEmail("user@example.com");
        assertTrue(UserValidator.isValid(validUser));

        // Пустой email
        validUser.setEmail("");
        assertFalse(UserValidator.isValid(validUser));

        // null email
        validUser.setEmail(null);
        assertFalse(UserValidator.isValid(validUser));

        // Email без символа @
        validUser.setEmail("invalid-email");
        assertFalse(UserValidator.isValid(validUser));

        // Email с пробелом (недопустимо)
        validUser.setEmail(" user@example.com ");
        assertFalse(UserValidator.isValid(validUser));
    }

    @Test
    void shouldOnlyAcceptValidLogin() {
        // Валидный логин
        validUser.setLogin("validlogin");
        assertTrue(UserValidator.isValid(validUser));

        // Пустой логин
        validUser.setLogin("");
        assertFalse(UserValidator.isValid(validUser));

        // null логин
        validUser.setLogin(null);
        assertFalse(UserValidator.isValid(validUser));

        // Логин с пробелами
        validUser.setLogin("login with spaces");
        assertFalse(UserValidator.isValid(validUser));

        // Логин начинается с пробела
        validUser.setLogin(" leading");
        assertFalse(UserValidator.isValid(validUser));

        // Логин заканчивается пробелом
        validUser.setLogin("trailing ");
        assertFalse(UserValidator.isValid(validUser));
    }

    @Test
    void shouldAllowEmptyOrNullName() {
        // Имя может быть пустым — в этом случае будет использован логин
        validUser.setName("");
        assertTrue(UserValidator.isValid(validUser));

        validUser.setName(null);
        assertTrue(UserValidator.isValid(validUser));

        validUser.setName("  ");
        assertTrue(UserValidator.isValid(validUser));
    }

    @Test
    void shouldOnlyAcceptPastBirthday() {
        // Дата рождения в прошлом — допустима
        validUser.setBirthday(LocalDate.of(1990, 1, 1));
        assertTrue(UserValidator.isValid(validUser));

        validUser.setBirthday(LocalDate.now());
        assertTrue(UserValidator.isValid(validUser));

        // Дата в будущем
        validUser.setBirthday(LocalDate.now().plusDays(1));
        assertFalse(UserValidator.isValid(validUser));
    }

    @Test
    void shouldRejectUserWithAllInvalidFields() {
        // Все поля неверны
        validUser.setEmail("invalid-email");
        validUser.setLogin(" ");
        validUser.setBirthday(LocalDate.now().plusYears(1));
        validUser.setName("");

        assertFalse(UserValidator.isValid(validUser));
    }

    @Test
    void shouldAcceptUserWithMinimalValidData() {
        // Минимально допустимые значения
        validUser.setEmail("a@b.co");
        validUser.setLogin("login");
        validUser.setBirthday(LocalDate.of(1900, 1, 1));
        validUser.setName(""); // разрешено

        assertTrue(UserValidator.isValid(validUser));
    }
}