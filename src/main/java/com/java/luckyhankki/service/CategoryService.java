package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.category.Category;
import com.java.luckyhankki.domain.category.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public Category registerCategory(Category category) {
        if (repository.existsByName(category.getName())) {
            throw new RuntimeException("이미 존재하는 카테고리 입니다.");
        }

        return repository.save(category);
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("아이디에 해당되는 카테고리가 존재하지 않습니다."));
    }

    public void updateCategoryName(long id, String newName) {
        Category result = repository.findById(id)
                                    .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));
        result.setName(newName);
    }
}
