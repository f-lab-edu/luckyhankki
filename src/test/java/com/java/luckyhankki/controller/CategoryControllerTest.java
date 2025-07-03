package com.java.luckyhankki.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.luckyhankki.domain.category.Category;
import com.java.luckyhankki.exception.CustomException;
import com.java.luckyhankki.exception.ErrorCode;
import com.java.luckyhankki.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@WithMockUser(username = "test", roles = "ADMIN")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("카테고리 등록 웹 테스트")
    void createCategory() throws Exception {
        //given
        Category category = new Category("음식");

        given(categoryService.registerCategory(any(Category.class)))
                .willReturn(category);

        createCategory(category);
    }

    @Test
    @DisplayName("모든 카테고리 조회 웹 테스트")
    void createAndGetAllCategories() throws Exception {
        List<Category> categories = List.of(
                new Category("음식"),
                new Category("베이커리"),
                new Category("식료품")
        );

        given(categoryService.registerCategory(any(Category.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        given(categoryService.getAllCategories()).willReturn(categories);

        for (Category category : categories) {
            createCategory(category);
        }

        mockMvc.perform(get("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(categories.size()))
                .andExpect(jsonPath("$[0].name").value(categories.get(0).getName()))
                .andExpect(jsonPath("$[1].name").value(categories.get(1).getName()))
                .andExpect(jsonPath("$[2].name").value(categories.get(2).getName()))
                .andDo(print());
    }

    @Test
    @DisplayName("카테고리id로 카테고리명 조회 웹 테스트")
    void getCategoryById() throws Exception {
        Category category = new Category("음식점");

        given(categoryService.registerCategory(any(Category.class)))
                .willReturn(category);

        createCategory(category);

        given(categoryService.getCategoryById(any(Long.class))).willReturn(category);

        mockMvc.perform(get("/categories/{categoryId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(category.getName()));
    }

    @Test
    @DisplayName("카테고리 조회 실패 웹 테스트")
    void getCategoryById_throwsException() throws Exception {
        Category category = new Category("음식점");

        given(categoryService.registerCategory(any(Category.class)))
                .willReturn(category);

        createCategory(category);

        given(categoryService.getCategoryById(1L))
                .willThrow(new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        mockMvc.perform(get("/categories/{categoryId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    private void createCategory(Category category) throws Exception {
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(category.getName()));
    }
}