package com.example.indproTest.service;

import com.example.indproTest.dto.TaskRequest;
import com.example.indproTest.dto.TaskResponse;
import com.example.indproTest.entity.Category;
import com.example.indproTest.entity.Task;
import com.example.indproTest.entity.User;
import com.example.indproTest.exception.ResourceNotFoundException;
import com.example.indproTest.exception.UnauthorizedException;
import com.example.indproTest.repository.CategoryRepository;
import com.example.indproTest.repository.TaskRepository;
import com.example.indproTest.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    public TaskResponse createTask(TaskRequest taskRequest, String username) {
        log.debug("Creating task for user: {}, request: {}", username, taskRequest);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new ResourceNotFoundException("User", 0L);
                });

        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setCompleted(false);
        task.setUser(user);

        if (taskRequest.getCategories() != null && !taskRequest.getCategories().isEmpty()) {
            log.debug("Processing {} categories for task", taskRequest.getCategories().size());
            Set<Category> categories = new HashSet<>();
            for (String categoryName : taskRequest.getCategories()) {
                Category category = categoryRepository.findByName(categoryName)
                        .orElseGet(() -> {
                            log.debug("Creating new category: {}", categoryName);
                            Category newCategory = new Category();
                            newCategory.setName(categoryName);
                            return categoryRepository.save(newCategory);
                        });
                categories.add(category);
            }
            task.setCategories(categories);
        }

        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with id: {}", savedTask.getId());
        return convertToTaskResponse(savedTask);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasks(String username, Boolean completed, String category, String search) {
        log.debug("Fetching tasks for user: {}, completed: {}, category: {}, search: {}", 
                username, completed, category, search);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new ResourceNotFoundException("User", 0L);
                });

        List<Task> tasks;
        if (search != null && !search.isEmpty()) {
            log.debug("Searching tasks with title containing: {}", search);
            tasks = taskRepository.findByUserIdAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(user.getId(), search);
        } else if (completed != null) {
            log.debug("Filtering tasks by completion status: {}", completed);
            tasks = taskRepository.findByUserIdAndCompletedOrderByCreatedAtDesc(user.getId(), completed);
        } else {
            log.debug("Fetching all tasks for user");
            tasks = taskRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        }

        if (category != null && !category.isEmpty()) {
            log.debug("Filtering tasks by category: {}", category);
            tasks = tasks.stream()
                    .filter(task -> task.getCategories().stream()
                            .anyMatch(c -> c.getName().equalsIgnoreCase(category)))
                    .collect(Collectors.toList());
        }

        log.info("Found {} tasks for user: {}", tasks.size(), username);
        return tasks.stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskResponse updateTask(Long taskId, TaskRequest taskRequest, String username) {
        log.debug("Updating task id: {} for user: {}, request: {}", taskId, username, taskRequest);
        
        Task task = findTaskAndValidateUser(taskId, username);
        
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());

        if (taskRequest.getCategories() != null) {
            log.debug("Updating {} categories for task", taskRequest.getCategories().size());
            Set<Category> categories = new HashSet<>();
            for (String categoryName : taskRequest.getCategories()) {
                Category category = categoryRepository.findByName(categoryName)
                        .orElseGet(() -> {
                            log.debug("Creating new category: {}", categoryName);
                            Category newCategory = new Category();
                            newCategory.setName(categoryName);
                            return categoryRepository.save(newCategory);
                        });
                categories.add(category);
            }
            task.setCategories(categories);
        }

        Task updatedTask = taskRepository.save(task);
        log.info("Task updated successfully: {}", updatedTask.getId());
        return convertToTaskResponse(updatedTask);
    }

    @Transactional
    public TaskResponse toggleTaskCompletion(Long taskId, String username) {
        log.debug("Toggling completion for task id: {} for user: {}", taskId, username);
        
        Task task = findTaskAndValidateUser(taskId, username);
        task.setCompleted(!task.isCompleted());
        
        Task updatedTask = taskRepository.save(task);
        log.info("Task completion toggled to {} for task: {}", updatedTask.isCompleted(), updatedTask.getId());
        return convertToTaskResponse(updatedTask);
    }

    @Transactional
    public void deleteTask(Long taskId, String username) {
        log.debug("Deleting task id: {} for user: {}", taskId, username);
        
        Task task = findTaskAndValidateUser(taskId, username);
        taskRepository.delete(task);
        log.info("Task deleted successfully: {}", taskId);
    }

    private Task findTaskAndValidateUser(Long taskId, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.error("Task not found with id: {}", taskId);
                    return new ResourceNotFoundException("Task", taskId);
                });

        if (!task.getUser().getUsername().equals(username)) {
            log.error("User {} attempted to access task {} owned by {}", 
                    username, taskId, task.getUser().getUsername());
            throw new UnauthorizedException("task");
        }

        return task;
    }

    private TaskResponse convertToTaskResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setCompleted(task.isCompleted());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        response.setCategories(task.getCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toSet()));
        return response;
    }
}
