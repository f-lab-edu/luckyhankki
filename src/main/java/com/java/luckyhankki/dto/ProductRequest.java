package com.java.luckyhankki.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.java.luckyhankki.config.annotation.PickupDate;
import com.java.luckyhankki.config.annotation.WonUnit;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record ProductRequest(
        @NotNull(message = "카테고리는 필수 선택입니다.")
        long categoryId,

        @NotBlank(message = "상품명은 반드시 입력해야 합니다.")
        String name,

        @NotNull
        @PositiveOrZero(message = "원래 가격은 0원 이상이어야 합니다.")
        @WonUnit(unit = 10)
        int priceOriginal,

        @NotNull
        @PositiveOrZero(message = "할인 가격은 0원 이상이어야 합니다.")
        @WonUnit(unit = 10)
        int priceDiscount,

        @NotNull
        @Positive(message = "수량은 1개 이상이어야 합니다.")
        int stock,

        String description,

        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        @FutureOrPresent(message = "픽업 시작 시간은 현재 시각 이후여야 합니다.")
        @PickupDate
        LocalDateTime pickupStartDateTime,

        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        @FutureOrPresent(message = "픽업 종료 시간은 현재 시각 이후여야 합니다.")
        @PickupDate
        LocalDateTime pickupEndDateTime
) {
}
