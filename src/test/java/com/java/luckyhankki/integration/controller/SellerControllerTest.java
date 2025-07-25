package com.java.luckyhankki.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.luckyhankki.controller.SellerController;
import com.java.luckyhankki.dto.seller.SellerRequest;
import com.java.luckyhankki.dto.seller.SellerResponse;
import com.java.luckyhankki.service.AuthService;
import com.java.luckyhankki.service.SellerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SellerController.class)
@WithMockUser(username = "test", roles = "SELLER")
class SellerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SellerService sellerService;

    @MockBean
    private AuthService authService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("판매자 회원가입 웹 테스트")
    void createSeller() throws Exception {
        // given
        SellerRequest request = new SellerRequest(
                "1234567890", "홍길동", "AAbc@!1234", "test@test.com"
        );
        SellerResponse response = new SellerResponse(
                "1234567890", "홍길동", "test@test.com"
        );

        given(sellerService.join(any(SellerRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/sellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.businessNumber").value("1234567890"))
                .andExpect(jsonPath("$.name").value("홍길동"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }
}