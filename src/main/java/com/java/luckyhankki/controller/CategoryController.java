package com.java.luckyhankki.controller;

import com.java.luckyhankki.domain.category.Category;
import com.java.luckyhankki.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Category> registerCategory(@RequestBody Category category) {
        Category result = service.registerCategory(category);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping
    public ResponseEntity<List<Category>> getCategories() {
        List<Category> categories = service.getAllCategories();

        return ResponseEntity.status(HttpStatus.OK).body(categories);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<Category> getCategory(@PathVariable long categoryId) {
        Category result = service.getCategoryById(categoryId);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<Void> updateCategory(@PathVariable long categoryId, @RequestBody Category category) {
        service.updateCategoryName(categoryId, category.getName());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
