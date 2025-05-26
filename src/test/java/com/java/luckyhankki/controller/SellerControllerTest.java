package com.java.luckyhankki.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.luckyhankki.dto.SellerRequestDTO;
import com.java.luckyhankki.dto.SellerResponseDTO;
import com.java.luckyhankki.service.SellerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SellerController.class)
class SellerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SellerService sellerService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("판매자 회원가입 웹 테스트")
    void createSeller() throws Exception {
        // given
        SellerRequestDTO requestDTO = new SellerRequestDTO(
                "1234567890", "홍길동", "abc1234", "test@test.com"
        );
        SellerResponseDTO responseDTO = new SellerResponseDTO(
                "1234567890", "홍길동", "test@test.com"
        );

        given(sellerService.join(any(SellerRequestDTO.class)))
                .willReturn(responseDTO);

        // when & then
        mockMvc.perform(post("/sellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.businessNumber").value("1234567890"))
                .andExpect(jsonPath("$.name").value("홍길동"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }
}