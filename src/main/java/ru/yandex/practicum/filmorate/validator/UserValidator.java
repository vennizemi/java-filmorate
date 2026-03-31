package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserValidator {

    public static boolean isValid(User user) {
        if (user == null) return false;
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) return false;
        if (!user.getEmail().contains("@")) return false;
        if (user.getEmail().startsWith(" ") || user.getEmail().endsWith(" ")) return false;

        if (user.getLogin() == null || user.getLogin().trim().isEmpty()) return false;
        if (user.getLogin().contains(" ")) return false;

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) return false;

        return true;
    }
}