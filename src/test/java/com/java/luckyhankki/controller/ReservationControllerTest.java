package com.java.luckyhankki.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.luckyhankki.domain.reservation.ReservationStatus;
import com.java.luckyhankki.dto.reservation.ReservationRequest;
import com.java.luckyhankki.dto.reservation.ReservationResponse;
import com.java.luckyhankki.service.ReservationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
@WithMockUser(username = "test", roles = "CUSTOMER")
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("상품 예약 웹 테스트")
    void reserveProduct() throws Exception {
        ReservationRequest request = new ReservationRequest(1L, 1L, 2);
        ReservationResponse response = new ReservationResponse("럭키비빔밥세트", 2, ReservationStatus.CONFIRMED.name());

        given(service.reserveProduct(request)).willReturn(response);

        System.out.println(objectMapper.writeValueAsString(response));

        mockMvc.perform(post("/reservations")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName").value(response.productName()))
                .andExpect(jsonPath("$.quantity").value(response.quantity()))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andDo(print());

        verify(service).reserveProduct(request);
    }
}