package com.java.luckyhankki.controller;

import com.java.luckyhankki.domain.reservation.ReservationProjection;
import com.java.luckyhankki.dto.reservation.ReservationDetailResponse;
import com.java.luckyhankki.dto.reservation.ReservationListResponse;
import com.java.luckyhankki.dto.reservation.ReservationRequest;
import com.java.luckyhankki.dto.reservation.ReservationResponse;
import com.java.luckyhankki.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Reservation", description = "예약 관련 API")
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @Operation(summary = "상품 예약", description = "사용자가 상품을 예약합니다.")
    @PostMapping
    public ResponseEntity<ReservationResponse> reserveProduct(@RequestBody ReservationRequest request) {
        ReservationResponse reservation = service.reserveProduct(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }

    @Operation(summary = "사용자의 예약 목록 조회", description = "사용자 ID에 해당하는 예약 목록을 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<List<ReservationListResponse>> getUserReservations(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {

        List<ReservationListResponse> userReservations = service.getUserReservations(userId);

        return ResponseEntity.status(HttpStatus.OK).body(userReservations);
    }

    @Operation(summary = "예약 목록 단건 조회", description = "사용자가 예약한 목록 중 하나의 예약 내역을 조회합니다.")
    @GetMapping("/{userId}/{reservationId}")
    public ResponseEntity<ReservationDetailResponse> getUserReservation(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "예약 ID") @PathVariable Long reservationId) {

        ReservationDetailResponse userReservation = service.getUserReservation(userId, reservationId);

        return ResponseEntity.status(HttpStatus.OK).body(userReservation);
    }

    @Operation(summary = "예약 취소", description = "예약을 취소합니다.")
    @DeleteMapping("/{userId}/{reservationId}")
    public ResponseEntity<Void> cancelUserReservation(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "예약 ID") @PathVariable Long reservationId) {

        service.cancelUserReservation(userId, reservationId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "가게에 들어온 예약 목록 조회", description = "가게 ID에 해당하는 예약 목록을 조회합니다.")
    @GetMapping("/stores/{storeId}")
    public ResponseEntity<List<ReservationProjection>> getStoreReservations(
            @Parameter(description = "가게 ID") @PathVariable Long storeId) {

        List<ReservationProjection> storeReservations = service.getStoreReservations(storeId);

        return ResponseEntity.status(HttpStatus.OK).body(storeReservations);
    }
}
