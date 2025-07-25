package com.java.luckyhankki.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.luckyhankki.controller.UserController;
import com.java.luckyhankki.dto.user.UserRegisterResponse;
import com.java.luckyhankki.dto.user.UserRequest;
import com.java.luckyhankki.service.AuthService;
import com.java.luckyhankki.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@WithMockUser(username = "test", roles = "CUSTOMER")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 웹 테스트")
    void registerUser() throws Exception {
        UserRequest request = new UserRequest(
                "test@test.com",
                "abcAbc@123",
                "홍길동",
                "01011112222",
                "경기도 수원시",
                BigDecimal.valueOf(126.978414),
                BigDecimal.valueOf(37.566680)
        );

        UserRegisterResponse response = new UserRegisterResponse(
                1L,
                request.name(),
                request.email(),
                "CUSTOMER",
                LocalDateTime.now()
        );

        given(userService.registerUser(any(UserRequest.class)))
                .willReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(request.name()))
                .andExpect(jsonPath("$.email").value(request.email()))
                .andExpect(jsonPath("$.roleType").value(response.roleType()))
                .andDo(print());
    }
}