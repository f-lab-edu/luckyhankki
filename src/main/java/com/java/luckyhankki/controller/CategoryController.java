package com.java.luckyhankki.controller;

import com.java.luckyhankki.domain.category.Category;
import com.java.luckyhankki.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Category", description = "카테고리 관련 API")
@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @Operation(summary = "카테고리 등록", description = "카테고리를 새로 생성합니다.")
    @PostMapping
    public ResponseEntity<Category> registerCategory(@RequestBody Category category) {
        Category result = service.registerCategory(category);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "카테고리 목록 조회", description = "등록된 모든 카테고리를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<Category>> getCategories() {
        List<Category> categories = service.getAllCategories();

        return ResponseEntity.status(HttpStatus.OK).body(categories);
    }

    @Operation(summary = "카테고리 조회", description = "카테고리 ID에 해당하는 카테고리를 조회합니다.")
    @GetMapping("/{categoryId}")
    public ResponseEntity<Category> getCategory(@Parameter(description = "카테고리 ID") @PathVariable long categoryId) {
        Category result = service.getCategoryById(categoryId);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Operation(summary = "카테고리 수정", description = "카테고리 ID에 해당하는 카테고리의 이름을 수정합니다.")
    @PutMapping("/{categoryId}")
    public ResponseEntity<Void> updateCategory(@Parameter(description = "카테고리 ID") @PathVariable long categoryId,
                                               @RequestBody Category category) {
        service.updateCategoryName(categoryId, category.getName());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
