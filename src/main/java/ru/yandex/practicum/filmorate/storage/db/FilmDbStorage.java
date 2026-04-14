package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert filmInsert;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
    }



    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT f.*, m.id as mpa_id, m.name as mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id " +
                "ORDER BY f.id";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Film create(Film film) {
        log.info("Creating film: {}", film);
        var parameters = new java.util.HashMap<String, Object>();
        parameters.put("name", film.getName());
        parameters.put("description", film.getDescription());
        parameters.put("release_date", film.getReleaseDate());
        parameters.put("duration", film.getDuration());
        parameters.put("mpa_rating_id", film.getMpa() != null ? film.getMpa().getId() : null);

        Number key = filmInsert.executeAndReturnKey(parameters);
        film.setId(key.longValue());
        log.info("Created film with id: {}", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        log.info("Updating film: {}", film);
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? WHERE id = ?";
        int updated = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId());
        if (updated == 0) {
            throw new IllegalArgumentException("Film with id " + film.getId() + " not found");
        }
        log.info("Updated film with id: {}", film.getId());
        return film;
    }

    @Override
    public Film findById(Long id) {
        try {
            String sql = "SELECT f.*, m.id as mpa_id, m.name as mpa_name " +
                    "FROM films f " +
                    "LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id " +
                    "WHERE f.id = ?";
            return jdbcTemplate.queryForObject(sql, this::mapRowToFilm, id);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Film with id " + id + " not found", e);
        }
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        MPA mpa = null;
        if (rs.getObject("mpa_id") != null) {
            mpa = MPA.builder()
                    .id(rs.getInt("mpa_id"))
                    .name(rs.getString("mpa_name"))
                    .build();
        }

        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date") != null ? rs.getDate("release_date").toLocalDate() : null)
                .duration(rs.getInt("duration"))
                .mpa(mpa)
                .build();
    }
}