package com.java.luckyhankki.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.luckyhankki.controller.AdminController;
import com.java.luckyhankki.dto.admin.AdminSellerResponse;
import com.java.luckyhankki.dto.admin.AdminStoreResponse;
import com.java.luckyhankki.dto.admin.AdminStoreWithSellerResponse;
import com.java.luckyhankki.service.AdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@WithMockUser(username = "test", roles = "ADMIN")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("가게 목록 조회 웹 테스트")
    void findAllStore() throws Exception {
        AdminStoreResponse store1 = new AdminStoreResponse(
                1L,
                "길동카페",
                "021231234",
                "경기도 수원시",
                true,
                0,
                LocalDateTime.now()
        );
        AdminStoreResponse store2 = new AdminStoreResponse(
                2L,
                "맥 디저트",
                "0311231234",
                "경기도 안양시",
                false,
                1,
                LocalDateTime.now()
        );
        List<AdminStoreResponse> responses = List.of(store1, store2);

        PageRequest pageRequest = PageRequest.of(0, 50, Sort.by(Sort.Direction.ASC, "id"));
        Page<AdminStoreResponse> storePage = new PageImpl<>(responses, pageRequest, responses.size());

        given(adminService.findAllStore(pageRequest)).willReturn(storePage);

        mockMvc.perform(get("/admins/stores")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(responses.size()))
                .andExpect(jsonPath("$.content[0].name").value("길동카페"))
                .andExpect(jsonPath("$.content[1].isApproved").value(false))
                .andDo(print());

        verify(adminService).findAllStore(pageRequest);
    }

    @Test
    @DisplayName("가게 및 판매자 조회 웹 테스트")
    void findStoreByStoreId() throws Exception {
        long storeId = 1L;
        AdminStoreResponse store = new AdminStoreResponse(
                storeId,
                "길동카페",
                "021231234",
                "경기도 수원시",
                true,
                0,
                LocalDateTime.now()
        );

        AdminSellerResponse seller = new AdminSellerResponse(
                1L,
                "1234567890",
                "홍길동",
                "test@test.com"
        );
        AdminStoreWithSellerResponse storeWithSeller = new AdminStoreWithSellerResponse(store, seller);

        given(adminService.findStoreByStoreId(storeId)).willReturn(storeWithSeller);

        mockMvc.perform(get("/admins/stores/{storeId}", storeId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adminStoreResponse.name").value("길동카페"))
                .andExpect(jsonPath("$.adminSellerResponse.sellerName").value("홍길동"))
                .andDo(print());

        verify(adminService).findStoreByStoreId(storeId);
    }

    @Test
    @DisplayName("가게 승인 웹 테스트")
    void approveStore() throws Exception {
        long storeId = 1L;

        mockMvc.perform(put("/admins/stores/{storeId}", storeId)
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(adminService).approveStore(storeId);
    }
}