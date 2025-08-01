package com.java.luckyhankki.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.luckyhankki.config.security.CustomUserDetails;
import com.java.luckyhankki.controller.StoreController;
import com.java.luckyhankki.dto.product.ProductResponse;
import com.java.luckyhankki.dto.store.StoreRequest;
import com.java.luckyhankki.dto.store.StoreResponse;
import com.java.luckyhankki.service.ProductService;
import com.java.luckyhankki.service.StoreService;
import com.java.luckyhankki.service.UnifiedUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StoreController.class)
@WithUserDetails(
        value = "1234567890",
        userDetailsServiceBeanName = "unifiedUserDetailsService",
        setupBefore = TestExecutionEvent.TEST_EXECUTION
)
class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreService storeService;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean(name = "unifiedUserDetailsService")
    private UnifiedUserDetailsService unifiedUserDetailsService;

    @BeforeEach
    void setUp() {
        CustomUserDetails mockUserDetails = new CustomUserDetails(
                1L,
                "1234567890",
                "password123@",
                Set.of(new SimpleGrantedAuthority("ROLE_SELLER"))
        );

        doReturn(mockUserDetails)
                .when(unifiedUserDetailsService)
                .loadUserByUsername("1234567890");
    }

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

        mockMvc.perform(post("/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequest))
                        .with(csrf()))
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

        mockMvc.perform(get("/stores")
                        .with(csrf()))
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

        mockMvc.perform(get("/stores/products")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andDo(print());

        verify(productService).getAllProductsByStore(storeId, null);
    }
}