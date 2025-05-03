package com.example.indproTest.repository;

import com.example.indproTest.entity.Category;
import com.example.indproTest.entity.Task;
import com.example.indproTest.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Repository
public class TaskRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Task> taskRowMapper = (rs, rowNum) -> {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setCompleted(rs.getBoolean("completed"));
        task.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        task.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        
        // Map the complete user information
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        task.setUser(user);
        
        return task;
    };

    public Task save(Task task) {
        if (task.getId() == null) {
            return insertTask(task);
        }
        return updateTask(task);
    }

    private Task insertTask(Task task) {
        String sql = "INSERT INTO tasks (title, description, completed, user_id, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setBoolean(3, task.isCompleted());
            ps.setLong(4, task.getUser().getId());
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            return ps;
        }, keyHolder);

        task.setId(keyHolder.getKey().longValue());
        
        // Save task categories
        if (task.getCategories() != null && !task.getCategories().isEmpty()) {
            for (Category category : task.getCategories()) {
                jdbcTemplate.update(
                    "INSERT INTO task_categories (task_id, category_id) VALUES (?, ?)",
                    task.getId(), category.getId()
                );
            }
        }
        
        return task;
    }

    private Task updateTask(Task task) {
        String sql = "UPDATE tasks SET title = ?, description = ?, completed = ?, updated_at = ? WHERE id = ? AND user_id = ?";
        
        int updated = jdbcTemplate.update(sql,
            task.getTitle(),
            task.getDescription(),
            task.isCompleted(),
            Timestamp.valueOf(LocalDateTime.now()),
            task.getId(),
            task.getUser().getId()
        );
        
        if (updated == 0) {
            throw new RuntimeException("Task not found with id: " + task.getId());
        }

        // Update task categories
        jdbcTemplate.update("DELETE FROM task_categories WHERE task_id = ?", task.getId());
        
        if (task.getCategories() != null && !task.getCategories().isEmpty()) {
            for (Category category : task.getCategories()) {
                jdbcTemplate.update(
                    "INSERT INTO task_categories (task_id, category_id) VALUES (?, ?)",
                    task.getId(), category.getId()
                );
            }
        }
        
        return task;
    }

    public Optional<Task> findById(Long id) {
        String sql = "SELECT t.*, u.id as user_id, u.username, u.email FROM tasks t " +
                    "JOIN users u ON t.user_id = u.id " +
                    "WHERE t.id = ?";
        List<Task> tasks = jdbcTemplate.query(sql, taskRowMapper, id);
        
        if (tasks.isEmpty()) {
            return Optional.empty();
        }
        
        Task task = tasks.get(0);
        loadTaskCategories(task);
        return Optional.of(task);
    }

    public List<Task> findByUserIdOrderByCreatedAtDesc(Long userId) {
        String sql = "SELECT t.*, u.id as user_id, u.username, u.email FROM tasks t " +
                    "JOIN users u ON t.user_id = u.id " +
                    "WHERE t.user_id = ? ORDER BY t.created_at DESC";
        List<Task> tasks = jdbcTemplate.query(sql, taskRowMapper, userId);
        tasks.forEach(this::loadTaskCategories);
        return tasks;
    }

    public List<Task> findByUserIdAndCompletedOrderByCreatedAtDesc(Long userId, boolean completed) {
        String sql = "SELECT t.*, u.id as user_id, u.username, u.email FROM tasks t " +
                    "JOIN users u ON t.user_id = u.id " +
                    "WHERE t.user_id = ? AND t.completed = ? ORDER BY t.created_at DESC";
        List<Task> tasks = jdbcTemplate.query(sql, taskRowMapper, userId, completed);
        tasks.forEach(this::loadTaskCategories);
        return tasks;
    }

    public List<Task> findByUserIdAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(Long userId, String title) {
        String sql = "SELECT t.*, u.id as user_id, u.username, u.email FROM tasks t " +
                    "JOIN users u ON t.user_id = u.id " +
                    "WHERE t.user_id = ? AND LOWER(t.title) LIKE LOWER(?) ORDER BY t.created_at DESC";
        List<Task> tasks = jdbcTemplate.query(sql, taskRowMapper, userId, "%" + title + "%");
        tasks.forEach(this::loadTaskCategories);
        return tasks;
    }

    public void delete(Task task) {
        jdbcTemplate.update("DELETE FROM task_categories WHERE task_id = ?", task.getId());
        jdbcTemplate.update("DELETE FROM tasks WHERE id = ? AND user_id = ?", task.getId(), task.getUser().getId());
    }

    private void loadTaskCategories(Task task) {
        String sql = "SELECT c.* FROM categories c " +
                    "JOIN task_categories tc ON c.id = tc.category_id " +
                    "WHERE tc.task_id = ?";
        
        List<Category> categories = jdbcTemplate.query(sql,
            (rs, rowNum) -> {
                Category category = new Category();
                category.setId(rs.getLong("id"));
                category.setName(rs.getString("name"));
                return category;
            },
            task.getId()
        );
        
        task.setCategories(new HashSet<>(categories));
    }
}
