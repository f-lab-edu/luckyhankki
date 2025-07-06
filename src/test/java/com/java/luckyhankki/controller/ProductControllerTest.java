package com.java.luckyhankki.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.luckyhankki.config.security.CustomUserDetails;
import com.java.luckyhankki.dto.product.*;
import com.java.luckyhankki.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@WithMockUser(username = "test", roles = "SELLER")
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

        ProductResponse product = new ProductResponse(
                1L,
                "가게명1",
                "음식",
                productRequest.name(),
                productRequest.priceOriginal(),
                productRequest.priceDiscount(),
                productRequest.stock(),
                productRequest.pickupStartDateTime(),
                productRequest.pickupEndDateTime()
        );

        given(productService.addProduct(eq(storeId), any(ProductRequest.class)))
                .willReturn(product);

        mockMvc.perform(post("/products")
                        .param("storeId", storeId.toString())
                        .content(objectMapper.writeValueAsString(productRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(product.name()))
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("유효성 검사에 실패했습니다."))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("픽업 날짜 유효성 검사 테스트")
    void addProduct_withInvalidPickupDate_throwsException() throws Exception {
        long storeId = 1L;
        ProductRequest invalidRequest = new ProductRequest(
                1L,
                "비빔밥",
                10000,
                8000,
                1,
                "픽업 날짜는 오늘 또는 내일만 가능합니다.",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5)
        );

        mockMvc.perform(post("/products")
                        .param("storeId", Long.toString(storeId))
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("유효성 검사에 실패했습니다."))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("상품 id에 해당하는 상품 조회")
    void getProduct() throws Exception {
        Long productId = 1L;
        ProductDetailResponse product = new ProductDetailResponse(
                "가게명1",
                "경기도 수원시",
                "031-1234-5678",
                "음식",
                "육회비빔밥",
                "한우육회비빔밥 입니다.",
                1,
                10000,
                8000,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(6)
        );

        given(productService.getProduct(productId)).willReturn(product);

        mockMvc.perform(get("/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storeName").value(product.storeName()))
                .andExpect(jsonPath("$.categoryName").value(product.categoryName()))
                .andExpect(jsonPath("$.description").value(product.description()))
                .andDo(print());

        verify(productService).getProduct(productId);
    }

    @Test
    @DisplayName("모든 상품 조회 웹 테스트")
    void getAllProducts() throws Exception {
        ProductResponse productResponse1 = new ProductResponse(
                1L,
                "가게A",
                "음식",
                "비빔밥",
                10000,
                8000,
                4,
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(6)
        );
        ProductResponse productResponse2 = new ProductResponse(
                2L,
                "가게B",
                "베이커리",
                "랜덤빵박스",
                25000,
                20000,
                2,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(9)
        );

        List<ProductResponse> content = List.of(productResponse1, productResponse2);
        Pageable pageable = PageRequest.of(0, 2);
        Slice<ProductResponse> slice = new SliceImpl<>(content, pageable, true);

        given(productService.getAllProducts(any(Pageable.class))).willReturn(slice);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].storeName").value(productResponse1.storeName()))
                .andExpect(jsonPath("$.content[1].name").value(productResponse2.name()))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.size").value(2))
                .andDo(print());

        verify(productService).getAllProducts(any(Pageable.class));
    }

    @Test
    @DisplayName("조회 조건에 따라 해당되는 모든 상품 조회 웹 테스트")
    void searchProductsByCondition() throws Exception {
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com"); // 필요 시 메서드 설정

        ProductResponse productResponse1 = new ProductResponse(
                1L,
                "가게A",
                "음식",
                "비빔밥",
                10000,
                8000,
                4,
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(6)
        );
        ProductResponse productResponse2 = new ProductResponse(
                2L,
                "가게B",
                "베이커리",
                "랜덤빵박스",
                25000,
                20000,
                2,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(9)
        );

        ProductSearchCondition condition = new ProductSearchCondition(null, null, null, null);

        // 거리 정보 포함 응답 DTO 생성
        ProductWithDistanceResponse response1 = new ProductWithDistanceResponse(
                productResponse1,
                BigDecimal.valueOf(2.5)
        );

        ProductWithDistanceResponse response2 = new ProductWithDistanceResponse(
                productResponse2,
                BigDecimal.valueOf(2.75)
        );

        List<ProductWithDistanceResponse> content = List.of(response1, response2);
        Pageable pageable = PageRequest.of(0, 2);
        Slice<ProductWithDistanceResponse> slice = new SliceImpl<>(content, pageable, true);

        given(productService.searchProductsByCondition(
                    any(CustomUserDetails.class),
                    any(ProductSearchCondition.class),
                    any(Pageable.class)))
                .willReturn(slice);

        mockMvc.perform(get("/products/condition")
                        .content(objectMapper.writeValueAsString(condition))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content[0].storeName").value(productResponse1.storeName()))
//                .andExpect(jsonPath("$.content[1].name").value(productResponse2.name()))
//                .andExpect(jsonPath("$.first").value(true))
//                .andExpect(jsonPath("$.size").value(2))
                .andDo(print());

        verify(productService).searchProductsByCondition(any(), any(), any());
    }

    @Test
    @DisplayName("상품 업데이트 웹 테스트")
    void updateProduct() throws Exception {
        Long productId = 1L;
        ProductUpdateRequest productUpdateRequest = new ProductUpdateRequest(
                null,
                "초밥",
                250000,
                null,
                null,
                "name, priceOriginal, description 변경",
                null,
                null
        );

        mockMvc.perform(put("/products/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productUpdateRequest))
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(productService).updateProduct(eq(productId), any(ProductUpdateRequest.class));
    }

    @Test
    @DisplayName("hard delete 웹 테스트")
    void deleteProduct() throws Exception {
        Long productId = 1L;

        mockMvc.perform(delete("/products/{productId}", productId)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(productService).deleteProduct(eq(productId));
    }
}