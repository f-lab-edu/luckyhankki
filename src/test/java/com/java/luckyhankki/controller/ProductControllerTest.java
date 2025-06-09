package com.java.luckyhankki.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.luckyhankki.domain.product.Product;
import com.java.luckyhankki.dto.ProductRequest;
import com.java.luckyhankki.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("상품 추가 웹 테스트")
    void addProduct() throws Exception {
        Long storeId = 1L;
        ProductRequest productRequest = new ProductRequest(
                1L,
                "비빔밥",
                10000,
                8000,
                1,
                "육회비빔밥입니다.",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(6)
        );

        Product product = new Product(
                null,
                null,
                productRequest.name(),
                productRequest.priceOriginal(),
                productRequest.priceDiscount(),
                productRequest.stock(),
                productRequest.description(),
                productRequest.pickupStartDateTime(),
                productRequest.pickupEndDateTime()
        );

        given(productService.addProduct(eq(storeId), any(ProductRequest.class)))
                .willReturn(product);

        mockMvc.perform(post("/products")
                        .param("storeId", storeId.toString())
                        .content(objectMapper.writeValueAsString(productRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andDo(print());

        verify(productService).addProduct(eq(storeId), any(ProductRequest.class));
    }

    @Test
    @DisplayName("한국 원 단위 유효성 검사 테스트")
    void addProduct_withInvalidPrice_throwsException() throws Exception {
        long storeId = 1L;
        ProductRequest invalidRequest = new ProductRequest(
                1L,
                "비빔밥",
                10009,
                8000,
                1,
                "한국 원화는 최소 10원 단위입니다.",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(6)
        );

        mockMvc.perform(post("/products")
                        .param("storeId", Long.toString(storeId))
                        .content(objectMapper.writeValueAsString(invalidRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("상품 id에 해당하는 상품 조회")
    void getProduct() throws Exception {
        Long productId = 1L;
        Product product = new Product(
                null,
                null,
                "비빔밥",
                10000,
                8000,
                1,
                "육회비빔밥입니다.",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(6)
        );

        given(productService.getProduct(productId)).willReturn(product);

        mockMvc.perform(get("/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andDo(print());

        verify(productService).getProduct(productId);
    }
}