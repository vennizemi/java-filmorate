package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class MPADbStorage implements MPAStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<MPA> findAll() {
        String sql = "SELECT * FROM mpa_ratings ORDER BY id";
        return jdbcTemplate.query(sql, this::mapRowToMPA);
    }

    @Override
    public MPA findById(int id) {
        try {
            String sql = "SELECT * FROM mpa_ratings WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, this::mapRowToMPA, id);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("MPA rating with id " + id + " not found", e);
        }
    }

    private MPA mapRowToMPA(ResultSet rs, int rowNum) throws SQLException {
        return MPA.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();
    }
}