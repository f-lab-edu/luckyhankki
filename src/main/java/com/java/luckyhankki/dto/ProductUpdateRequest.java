package com.java.luckyhankki.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.java.luckyhankki.config.annotation.PickupDate;
import com.java.luckyhankki.config.annotation.WonUnit;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

public record ProductUpdateRequest(
        Long categoryId,

        String name,

        @PositiveOrZero(message = "원래 가격은 0원 이상이어야 합니다.")
        @WonUnit(unit = 10)
        Integer priceOriginal,

        @PositiveOrZero(message = "할인 가격은 0원 이상이어야 합니다.")
        @WonUnit(unit = 10)
        Integer priceDiscount,

        @Positive(message = "수량은 1개 이상이어야 합니다.")
        Integer stock,

        String description,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        @FutureOrPresent(message = "픽업 시작 시간은 현재 시각 이후여야 합니다.")
        @PickupDate
        LocalDateTime pickupStartDateTime,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        @FutureOrPresent(message = "픽업 종료 시간은 현재 시각 이후여야 합니다.")
        @PickupDate
        LocalDateTime pickupEndDateTime
) {
}
