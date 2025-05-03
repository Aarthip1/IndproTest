package com.example.indproTest.controller;

import com.example.indproTest.dto.TaskRequest;
import com.example.indproTest.dto.TaskResponse;
import com.example.indproTest.service.TaskService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest taskRequest,
            Authentication authentication) {
        log.info("Creating task for user: {}, request: {}", authentication.getName(), taskRequest);
        TaskResponse task = taskService.createTask(taskRequest, authentication.getName());
        log.info("Task created successfully: {}", task);
        return ResponseEntity.ok(task);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks(
            Authentication authentication,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        log.info("Fetching tasks for user: {}, completed: {}, category: {}, search: {}", 
                authentication.getName(), completed, category, search);
        List<TaskResponse> tasks = taskService.getTasks(authentication.getName(), completed, category, search);
        log.info("Found {} tasks", tasks.size());
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest taskRequest,
            Authentication authentication) {
        log.info("Updating task id: {} for user: {}, request: {}", id, authentication.getName(), taskRequest);
        TaskResponse task = taskService.updateTask(id, taskRequest, authentication.getName());
        log.info("Task updated successfully: {}", task);
        return ResponseEntity.ok(task);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> toggleTaskCompletion(
            @PathVariable Long id,
            Authentication authentication) {
        log.info("Toggling completion for task id: {} for user: {}", id, authentication.getName());
        TaskResponse task = taskService.toggleTaskCompletion(id, authentication.getName());
        log.info("Task completion toggled successfully: {}", task);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            Authentication authentication) {
        log.info("Deleting task id: {} for user: {}", id, authentication.getName());
        taskService.deleteTask(id, authentication.getName());
        log.info("Task deleted successfully");
        return ResponseEntity.noContent().build();
    }
}
