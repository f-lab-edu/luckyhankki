package com.java.luckyhankki.dto.reservation;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "예약 응답 DTO")
public record ReservationResponse(

        @Schema(description = "상품명", example = "1인럭키세트")
        String productName,

        @Schema(description = "수량", example = "1")
        int quantity,

        @Schema(description = "예약 상태", example = "CONFIRMED")
        String status
) {
}
