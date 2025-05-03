package com.example.indproTest.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<String> categories;
}
