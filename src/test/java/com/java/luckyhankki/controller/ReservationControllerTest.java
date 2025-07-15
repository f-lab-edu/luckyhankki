package com.java.luckyhankki.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.luckyhankki.domain.reservation.ReservationProjection;
import com.java.luckyhankki.domain.reservation.ReservationStatus;
import com.java.luckyhankki.dto.reservation.ReservationRequest;
import com.java.luckyhankki.dto.reservation.ReservationResponse;
import com.java.luckyhankki.dto.reservation.StoreReservationDetailResponse;
import com.java.luckyhankki.service.ReservationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("상품 예약 웹 테스트")
    @WithMockUser(roles = "CUSTOMER")
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

    @Test
    @DisplayName("상품 예약 취소 웹 테스트")
    @WithMockUser(roles = "SELLER")
    void cancelReservationByUser() throws Exception {
        Long userId = 1L;
        Long reservationId = 1L;

        mockMvc.perform(delete("/reservations/{userId}/{reservationId}", userId, reservationId)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(service).cancelReservationByUser(userId, reservationId);
    }

    @Test
    @DisplayName("가게 예약 목록 조회 웹 테스트")
    @WithMockUser(roles = "SELLER")
    void getReservationListByStore() throws Exception {
        Long storeId = 1L;
        ReservationProjection mockProjection = new ReservationProjection() {
            @Override public Long getId() { return 1L; }
            @Override public String getProductName() { return "소금빵"; }
            @Override public Integer getDiscountPrice() { return 3000; }
            @Override public Integer getQuantity() { return 2; }
            @Override public Integer getTotalPrice() { return 6000; }
            @Override public String getStatus() { return "CONFIRMED"; }
            @Override public LocalDateTime getCreatedAt() { return LocalDateTime.now(); }
        };

        given(service.getReservationListByStore(storeId))
                .willReturn(List.of(mockProjection));

        mockMvc.perform(get("/reservations/stores/{storeId}", storeId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].productName").value("소금빵"))
                .andDo(print());

        verify(service).getReservationListByStore(storeId);
    }

    @Test
    @DisplayName("가게-예약 내역 상세 조회 웹 테스트")
    @WithMockUser(roles = "SELLER")
    void getDetailReservationByStore() throws Exception {
        long storeId = 1L;
        long reservationId = 1L;
        StoreReservationDetailResponse response = new StoreReservationDetailResponse(
                reservationId,
                "1인럭키세트",
                "홍길동",
                "5678",
                2,
                10000,
                8000,
                16000,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                ReservationStatus.CONFIRMED,
                LocalDateTime.now()
        );

        given(service.getReservationDetailsByStore(reservationId, storeId))
                .willReturn(response);

        mockMvc.perform(get("/reservations/stores/{storeId}/{reservationId}", storeId, reservationId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("1인럭키세트"))
                .andExpect(jsonPath("$.quantity").value(2))
                .andDo(print());

        verify(service).getReservationDetailsByStore(reservationId, storeId);
    }

    @Test
    @DisplayName("픽업 완료 처리 웹 테스트")
    @WithMockUser(roles = "SELLER")
    void updateStatusCompleted() throws Exception {
        Long reservationId = 1L;

        mockMvc.perform(put("/reservations/{reservationId}", reservationId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(service).updateStatusCompleted(reservationId);
    }
}