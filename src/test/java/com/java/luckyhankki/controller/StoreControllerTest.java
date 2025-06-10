package com.java.luckyhankki.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.luckyhankki.dto.ProductResponse;
import com.java.luckyhankki.dto.StoreRequest;
import com.java.luckyhankki.dto.StoreResponse;
import com.java.luckyhankki.service.ProductService;
import com.java.luckyhankki.service.StoreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StoreController.class)
class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreService storeService;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("가게 등록 웹 테스트")
    void registerStore() throws Exception {
        StoreRequest storeRequest = new StoreRequest(
                "가게명1",
                "02-1234-5678",
                "서울특별시 종로구 청와대로 1",
                BigDecimal.valueOf(126.978414),
                BigDecimal.valueOf(37.566680)
        );

        StoreResponse storeResponse = new StoreResponse(
                1L,
                "가게명1",
                "02-1234-5678",
                "서울특별시 종로구 청와대로 1",
                false,
                0
        );

        given(storeService.registerStore((any(Long.class)),any(StoreRequest.class)))
                .willReturn(storeResponse);

        mockMvc.perform(post("/stores?sellerId=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("가게명1"))
                .andExpect(jsonPath("$.phone").value("02-1234-5678"))
                .andExpect(jsonPath("$.address").value("서울특별시 종로구 청와대로 1"))
                .andExpect(jsonPath("$.isApproved").value(false))
                .andExpect(jsonPath("$.reportCount").value(0));
    }

    @Test
    @DisplayName("판매자ID에 해당하는 가게 정보 조회 웹 테스트")
    void getStore() throws Exception {
        StoreResponse storeResponse = new StoreResponse(
                1L,
                "가게명1",
                "02-1234-5678",
                "서울특별시 종로구 청와대로 1",
                false,
                0
        );

        given(storeService.findStore(any(Long.class)))
                .willReturn(storeResponse);

        mockMvc.perform(get("/stores/{sellerId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("가게명1"))
                .andExpect(jsonPath("$.phone").value("02-1234-5678"))
                .andExpect(jsonPath("$.address").value("서울특별시 종로구 청와대로 1"))
                .andExpect(jsonPath("$.isApproved").value(false))
                .andExpect(jsonPath("$.reportCount").value(0));
    }

    @Test
    @DisplayName("가게에 등록된 모든 상품 목록 조회 웹 테스트")
    void getProducts() throws Exception {
        long storeId = 1L;
        ProductResponse response1 = mock(ProductResponse.class);
        ProductResponse response2 = mock(ProductResponse.class);

        List<ProductResponse> products = List.of(response1, response2);

        given(productService.getAllProductsByStore(storeId, null))
                .willReturn(products);

        mockMvc.perform(get("/stores/{storeId}/products", storeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andDo(print());

        verify(productService).getAllProductsByStore(storeId, null);
    }
}