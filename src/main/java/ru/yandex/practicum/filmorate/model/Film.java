package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.FilmReleaseDateConstraint;
import ru.yandex.practicum.filmorate.validation.OnUpdate;

import java.time.LocalDate;

@Data
public class Film {

    @NotNull(message = "Id должен быть указан при обновлении", groups = OnUpdate.class)
    private Long id;
    @NotBlank(message = "название не может быть пустым")
    private String name;
    @Size(max = 200, message = "максимальная длина описания — 200 символов")
    private String description;

    @FilmReleaseDateConstraint
    private LocalDate releaseDate;

    @Positive(message = "продолжительность фильма должна быть положительным числом")
    private Integer duration;

}