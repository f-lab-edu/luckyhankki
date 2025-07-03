package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.category.Category;
import com.java.luckyhankki.domain.category.CategoryRepository;
import com.java.luckyhankki.exception.CustomException;
import com.java.luckyhankki.exception.ErrorCode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Category registerCategory(Category category) {
        try {
            return repository.save(category);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    @Transactional
    public void updateCategoryName(long id, String newName) {
        Category result = repository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
        result.changeName(newName);
    }
}
