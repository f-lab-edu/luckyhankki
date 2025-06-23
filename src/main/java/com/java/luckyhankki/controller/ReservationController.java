package com.java.luckyhankki.controller;

import com.java.luckyhankki.domain.reservation.ReservationProjection;
import com.java.luckyhankki.dto.reservation.*;
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
    public ResponseEntity<List<ReservationListResponse>> getReservationListByUser(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {

        List<ReservationListResponse> userReservations = service.getReservationListByUser(userId);

        return ResponseEntity.status(HttpStatus.OK).body(userReservations);
    }

    @Operation(summary = "예약 목록 단건 조회", description = "사용자가 예약한 목록 중 하나의 예약 내역을 조회합니다.")
    @GetMapping("/{userId}/{reservationId}")
    public ResponseEntity<ReservationDetailResponse> getReservationDetailByUser(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "예약 ID") @PathVariable Long reservationId) {

        ReservationDetailResponse userReservation = service.getReservationByUser(userId, reservationId);

        return ResponseEntity.status(HttpStatus.OK).body(userReservation);
    }

    @Operation(summary = "예약 취소", description = "예약을 취소합니다.")
    @DeleteMapping("/{userId}/{reservationId}")
    public ResponseEntity<Void> cancelReservationByUser(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "예약 ID") @PathVariable Long reservationId) {

        service.cancelReservationByUser(userId, reservationId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "가게에 들어온 예약 목록 조회",
            description = "가게 ID에 해당하는 예약 목록을 조회합니다. 예약 상태가 CONFIRMED와 COMPLETED만 조회합니다.")
    @GetMapping("/stores/{storeId}")
    public ResponseEntity<List<ReservationProjection>> getReservationListByStore(
            @Parameter(description = "가게 ID") @PathVariable Long storeId) {

        List<ReservationProjection> storeReservations = service.getReservationListByStore(storeId);

        return ResponseEntity.status(HttpStatus.OK).body(storeReservations);
    }

    @Operation(summary = "가게 예약 단건 상세 조회", description = "가게 입장에서 예약 내용을 상세 조회합니다.")
    @GetMapping("/stores/{storeId}/{reservationId}")
    public ResponseEntity<StoreReservationDetailResponse> getDetailReservationByStore(
            @Parameter(description = "가게 ID") @PathVariable Long storeId,
            @Parameter(description = "예약 ID") @PathVariable Long reservationId) {

        StoreReservationDetailResponse result = service.getReservationDetailsByStore(reservationId, storeId);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Operation(summary = "픽업 완료 처리", description = "사용자가 픽업하러 오면 가게는 예약 완료 처리를 합니다.")
    @PutMapping("/{reservationId}")
    public ResponseEntity<Void> updateStatusCompleted(
            @Parameter(description = "예약 ID") @PathVariable Long reservationId) {

        service.updateStatusCompleted(reservationId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
