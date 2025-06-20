package com.java.luckyhankki.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.java.luckyhankki.config.annotation.PickupDate;
import com.java.luckyhankki.config.annotation.WonUnit;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Schema(description = "상품 등록 요청 DTO")
public record ProductRequest(

        @Schema(description = "카테고리 ID", example = "1")
        @NotNull(message = "카테고리는 필수 선택입니다.")
        long categoryId,

        @Schema(description = "상품명", example = "1인럭키세트")
        @NotBlank(message = "상품명은 반드시 입력해야 합니다.")
        String name,

        @Schema(description = "상품 가격", example = "28000")
        @NotNull
        @PositiveOrZero(message = "원래 가격은 0원 이상이어야 합니다.")
        @WonUnit(unit = 10)
        int priceOriginal,

        @Schema(description = "할인가", example = "10000")
        @NotNull
        @PositiveOrZero(message = "할인 가격은 0원 이상이어야 합니다.")
        @WonUnit(unit = 10)
        int priceDiscount,

        @Schema(description = "상품 수량", example = "1")
        @NotNull
        @Positive(message = "수량은 1개 이상이어야 합니다.")
        int stock,

        @Schema(description = "상품 설명", example = "길동국밥의 시그니처 국밥과 럭키반찬들로 이루어진 구성품입니다.")
        String description,

        @Schema(description = "픽업 시작 일시", example = "2025-06-15 08:00")
        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        @FutureOrPresent(message = "픽업 시작 시간은 현재 시각 이후여야 합니다.")
        @PickupDate
        LocalDateTime pickupStartDateTime,

        @Schema(description = "픽업 종료 일시", example = "2025-06-15 13:00")
        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        @FutureOrPresent(message = "픽업 종료 시간은 현재 시각 이후여야 합니다.")
        @PickupDate
        LocalDateTime pickupEndDateTime
) {
}
