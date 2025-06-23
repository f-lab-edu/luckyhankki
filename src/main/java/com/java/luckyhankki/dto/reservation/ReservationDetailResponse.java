package com.java.luckyhankki.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "예약 상세 조회 응답 DTO")
public record ReservationDetailResponse(

        @Schema(description = "상품명", example = "1인럭키세트")
        String productName,

        @Schema(description = "상품 가격", example = "28,000")
        int priceOriginal,

        @Schema(description = "할인가", example = "10,000")
        int priceDiscount,

        @Schema(description = "픽업 시작 일시", example = "2025-06-15 08:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime pickupStartDateTime,

        @Schema(description = "픽업 종료 일시", example = "2025-06-15 13:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime pickupEndDateTime,

        @Schema(description = "수량", example = "1")
        int quantity,

        @Schema(description = "예약 상태", example = "CONFIRMED")
        String status,

        @Schema(description = "예약 생성 일시", example = "2025-06-22 19:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime createdAt
) {
}
