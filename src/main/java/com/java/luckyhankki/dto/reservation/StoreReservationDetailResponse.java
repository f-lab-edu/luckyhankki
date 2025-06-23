package com.java.luckyhankki.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.java.luckyhankki.domain.reservation.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "가게가 예약 상세 정보를 확인할 때 사용하는 응답 DTO")
public record StoreReservationDetailResponse (

        @Schema(description = "예약 ID", example = "1")
        long reservationId,

        @Schema(description = "상품명", example = "1인럭키세트")
        String productName,

        @Schema(description = "예약한 사용자명", example = "홍길동")
        String userName,

        @Schema(description = "예약한 사용자의 핸드폰 뒷 4자리", example = "5678")
        String userPhone,

        @Schema(description = "수량", example = "2")
        int quantity,

        @Schema(description = "상품 가격", example = "28,000")
        int priceOriginal,

        @Schema(description = "할인가", example = "10,000")
        int priceDiscount,

        @Schema(description = "총 금액(할인가*수량)", example = "20,000")
        int totalPrice,

        @Schema(description = "픽업 시작 일시", example = "2025-06-15 08:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime pickupStartDateTime,

        @Schema(description = "픽업 종료 일시", example = "2025-06-15 13:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime pickupEndDateTime,

        @Schema(description = "예약 상태", example = "CONFIRMED")
        ReservationStatus status,

        @Schema(description = "예약 생성 일시", example = "2025-06-22 19:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime createdAt
) {
}
