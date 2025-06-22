package com.java.luckyhankki.dto.reservation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReservationRequest(
        @NotNull
        long productId,

        @NotNull
        long userId,

        @NotNull
        @Positive(message = "수량은 1개 이상부터 선택 가능합니다.")
        int quantity
) {
}
