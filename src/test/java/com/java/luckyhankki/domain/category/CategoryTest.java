package com.java.luckyhankki.domain.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CategoryTest {

    @Autowired
    private CategoryRepository repository;

    @Test
    @DisplayName("카테고리 저장 완료 후 id로 조회 성공")
    void save_and_findById() {
        Category savedCategory = repository.save(new Category("음식"));
        Category findCategory = repository.findById(savedCategory.getId()).orElseThrow();

        assertThat(findCategory).isEqualTo(savedCategory);
        assertThat(findCategory.getName()).isEqualTo(savedCategory.getName());
    }

    @Test
    @DisplayName("카테고리 이름 변경 성공")
    void updateCategoryName() {
        Category category = repository.save(new Category("음식"));

        category.setName("베이커리");
        repository.save(category);

        Category updatedCategory = repository.findById(category.getId()).orElseThrow();
        assertThat(updatedCategory.getName()).isEqualTo("베이커리");
    }
}