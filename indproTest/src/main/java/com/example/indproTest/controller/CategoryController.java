package com.example.indproTest.controller;

import com.example.indproTest.entity.Category;
import com.example.indproTest.service.CategoryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        log.info("Fetching all categories");
        List<Category> categories = categoryService.getAllCategories();
        log.info("Found {} categories", categories.size());
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        log.info("Creating new category with name: {}", category.getName());
        Category createdCategory = categoryService.createCategory(category);
        log.info("Category created successfully: {}", createdCategory);
        return ResponseEntity.ok(createdCategory);
    }
}
