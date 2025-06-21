package com.java.luckyhankki.dto.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.java.luckyhankki.config.annotation.PickupDate;
import com.java.luckyhankki.config.annotation.WonUnit;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

@Schema(description = "상품 업데이트 요청 DTO")
public record ProductUpdateRequest(

        @Schema(description = "카테고리 ID", example = "1")
        Long categoryId,

        @Schema(description = "상품명", example = "1인럭키세트")
        String name,

        @Schema(description = "상품 가격", example = "28000")
        @PositiveOrZero(message = "원래 가격은 0원 이상이어야 합니다.")
        @WonUnit(unit = 10)
        Integer priceOriginal,

        @Schema(description = "할인가", example = "10000")
        @PositiveOrZero(message = "할인 가격은 0원 이상이어야 합니다.")
        @WonUnit(unit = 10)
        Integer priceDiscount,

        @Schema(description = "상품 수량", example = "1")
        @Positive(message = "수량은 1개 이상이어야 합니다.")
        Integer stock,

        @Schema(description = "상품 설명", example = "길동국밥의 시그니처 국밥과 럭키반찬들로 이루어진 구성품입니다.")
        String description,

        @Schema(description = "픽업 시작 일시", example = "2025-06-15 08:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        @FutureOrPresent(message = "픽업 시작 시간은 현재 시각 이후여야 합니다.")
        @PickupDate
        LocalDateTime pickupStartDateTime,

        @Schema(description = "픽업 종료 일시", example = "2025-06-15 13:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        @FutureOrPresent(message = "픽업 종료 시간은 현재 시각 이후여야 합니다.")
        @PickupDate
        LocalDateTime pickupEndDateTime
) {
}
