package com.example.indproTest.repository;

import com.example.indproTest.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        return user;
    };

    public User save(User user) {
        if (user.getId() == null) {
            String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getEmail());
                ps.setString(3, user.getPassword());
                return ps;
            }, keyHolder);

            user.setId(keyHolder.getKey().longValue());
        } else {
            jdbcTemplate.update(
                "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?",
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getId()
            );
        }
        return user;
    }

    public Optional<User> findById(Long id) {
        List<User> users = jdbcTemplate.query(
            "SELECT * FROM users WHERE id = ?",
            userRowMapper,
            id
        );
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    public Optional<User> findByUsername(String username) {
        List<User> users = jdbcTemplate.query(
            "SELECT * FROM users WHERE username = ?",
            userRowMapper,
            username
        );
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    public Optional<User> findByEmail(String email) {
        List<User> users = jdbcTemplate.query(
            "SELECT * FROM users WHERE email = ?",
            userRowMapper,
            email
        );
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    public boolean existsByUsername(String username) {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM users WHERE username = ?",
            Integer.class,
            username
        );
        return count != null && count > 0;
    }

    public boolean existsByEmail(String email) {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM users WHERE email = ?",
            Integer.class,
            email
        );
        return count != null && count > 0;
    }

    public void delete(User user) {
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", user.getId());
    }
}
