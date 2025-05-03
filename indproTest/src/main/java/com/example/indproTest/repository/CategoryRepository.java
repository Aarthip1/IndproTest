package com.example.indproTest.repository;

import com.example.indproTest.entity.Category;
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
public class CategoryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Category> categoryRowMapper = (rs, rowNum) -> {
        Category category = new Category();
        category.setId(rs.getLong("id"));
        category.setName(rs.getString("name"));
        return category;
    };

    public Category save(Category category) {
        if (category.getId() == null) {
            String sql = "INSERT INTO categories (name) VALUES (?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, category.getName());
                return ps;
            }, keyHolder);

            category.setId(keyHolder.getKey().longValue());
        } else {
            jdbcTemplate.update(
                "UPDATE categories SET name = ? WHERE id = ?",
                category.getName(),
                category.getId()
            );
        }
        return category;
    }

    public List<Category> findAll() {
        return jdbcTemplate.query("SELECT * FROM categories", categoryRowMapper);
    }

    public Optional<Category> findById(Long id) {
        List<Category> categories = jdbcTemplate.query(
            "SELECT * FROM categories WHERE id = ?",
            categoryRowMapper,
            id
        );
        return categories.isEmpty() ? Optional.empty() : Optional.of(categories.get(0));
    }

    public Optional<Category> findByName(String name) {
        List<Category> categories = jdbcTemplate.query(
            "SELECT * FROM categories WHERE name = ?",
            categoryRowMapper,
            name
        );
        return categories.isEmpty() ? Optional.empty() : Optional.of(categories.get(0));
    }

    public boolean existsByName(String name) {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM categories WHERE name = ?",
            Integer.class,
            name
        );
        return count != null && count > 0;
    }

    public void delete(Category category) {
        // First, remove all associations in the junction table
        jdbcTemplate.update("DELETE FROM task_categories WHERE category_id = ?", category.getId());
        // Then delete the category
        jdbcTemplate.update("DELETE FROM categories WHERE id = ?", category.getId());
    }
}
