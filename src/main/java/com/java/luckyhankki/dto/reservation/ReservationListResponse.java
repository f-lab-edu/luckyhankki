package com.java.luckyhankki.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "예약 목록 조회 응답 DTO")
public record ReservationListResponse(

        @Schema(description = "상품명", example = "1인럭키세트")
        String productName,

        @Schema(description = "할인가", example = "10,000")
        int priceDiscount,

        @Schema(description = "예약 상태", example = "CONFIRMED")
        String status,

        @Schema(description = "예약 생성 일시", example = "2025-06-22 19:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime createdAt
) {
}
