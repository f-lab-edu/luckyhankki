package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.category.Category;
import com.java.luckyhankki.domain.category.CategoryRepository;
import com.java.luckyhankki.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CategoryServiceTest {

    private final CategoryRepository repository = Mockito.mock(CategoryRepository.class);
    private final CategoryService service = new CategoryService(repository);

    @Test
    @DisplayName("카테고리 등록 성공")
    void registerCategory_success() {
        Category category = new Category("음식");

        when(repository.save(any(Category.class)))
                .then(returnsFirstArg());

        Category result = service.registerCategory(category);

        assertEquals("음식", result.getName());
        verify(repository).save(any());
    }

    @Test
    @DisplayName("카테고리명 중복 시 예외 발생")
    void registerCategory_throwsException_whenCategoryNameExists() {
        Category category = new Category("음식");

        when(repository.save(any(Category.class))).thenThrow(DataIntegrityViolationException.class);

        CustomException exception = assertThrows(CustomException.class, () -> service.registerCategory(category));

        assertEquals("이미 존재하는 카테고리 명입니다.", exception.getMessage());
        verify(repository).save(any());
    }

    @Test
    @DisplayName("등록된 모든 카테고리 조회 성공")
    void getAllCategories_success() {
        List<Category> categories = List.of(new Category("음식"),
                                            new Category("베이커리"),
                                            new Category("식료품"));

        when(repository.findAll()).thenReturn(categories);

        List<Category> resultList = service.getAllCategories();

        assertEquals(categories, resultList);
        verify(repository).findAll();
    }

    @Test
    @DisplayName("카테고리ID로 카테고리명 조회 성공")
    void getCategoryById_success() {
        Category category = new Category("음식");
        ReflectionTestUtils.setField(category, "id", 1L);
        when(repository.findById(1L)).thenReturn(Optional.of(category));

        Category result = service.getCategoryById(1L);
        assertEquals(category.getId(), result.getId());
        assertEquals("음식", result.getName());
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 카테고리ID로 조회 시 예외 발생")
    void getCategoryById_throwsException_whenCategoryIdNotExist() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> service.getCategoryById(1L));
        assertEquals("CATEGORY_NOT_FOUND", exception.getErrorCode().getCode());
        assertEquals("존재하지 않는 카테고리입니다.", exception.getErrorCode().getMessage());
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("카테고리명 변경 성공")
    void updateCategoryName_success() {
        Category category = new Category("음식");
        ReflectionTestUtils.setField(category, "id", 1L);
        when(repository.findById(1L)).thenReturn(Optional.of(category));

        service.updateCategoryName(1L, "베이커리");

        assertEquals("베이커리", category.getName());
        verify(repository).findById(1L);
    }
}