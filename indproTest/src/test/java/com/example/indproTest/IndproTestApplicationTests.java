package com.example.indproTest;

import com.example.indproTest.dto.SignUpRequest;
import com.example.indproTest.dto.TaskRequest;
import com.example.indproTest.dto.TaskResponse;
import com.example.indproTest.entity.User;
import com.example.indproTest.service.TaskService;
import com.example.indproTest.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class IndproTestApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Test
    @Sql(statements = "DELETE FROM users WHERE username = 'testuser'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testUserSignup() {
        // Arrange
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("testuser");
        signUpRequest.setEmail("test@example.com");
        signUpRequest.setPassword("password123");

        // Act
        User createdUser = userService.createUser(signUpRequest);

        // Assert
        assertNotNull(createdUser);
        assertThat(createdUser.getUsername()).isEqualTo("testuser");
        assertThat(createdUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @WithMockUser(username = "testuser")
    @Sql(statements = {
        "INSERT INTO users (username, email, password) VALUES ('testuser', 'test@example.com', '$2a$10$YourHashedPasswordHere')",
        "DELETE FROM tasks WHERE user_id = (SELECT id FROM users WHERE username = 'testuser')",
        "DELETE FROM users WHERE username = 'testuser'"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testCreateTask() {
        // Arrange
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTitle("Test Task");
        taskRequest.setDescription("Test Description");
        Set<String> categories = new HashSet<>();
        categories.add("work");
        taskRequest.setCategories(categories);

        // Act
        TaskResponse createdTask = taskService.createTask(taskRequest, "testuser");

        // Assert
        assertNotNull(createdTask);
        assertThat(createdTask.getTitle()).isEqualTo("Test Task");
        assertThat(createdTask.getDescription()).isEqualTo("Test Description");
        assertThat(createdTask.getCategories()).contains("work");
        assertThat(createdTask.isCompleted()).isFalse();
    }
}
