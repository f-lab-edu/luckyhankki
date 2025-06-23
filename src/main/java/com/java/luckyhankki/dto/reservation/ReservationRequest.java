package com.java.luckyhankki.dto.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "예약 요청 DTO")
public record ReservationRequest(

        @Schema(description = "상품 ID", example = "1")
        @NotNull
        long productId,

        @Schema(description = "유저 ID", example = "1")
        @NotNull
        long userId,

        @Schema(description = "수량", example = "2")
        @NotNull
        @Positive(message = "수량은 1개 이상부터 선택 가능합니다.")
        int quantity
) {
}
