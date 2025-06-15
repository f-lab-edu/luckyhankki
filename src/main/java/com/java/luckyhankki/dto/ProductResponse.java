package com.java.luckyhankki.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "상품 목록 조회 응답 DTO")
public record ProductResponse(

        @Schema(description = "상품 ID", example = "1")
        Long id,

        @Schema(description = "가게명", example = "길동국밥")
        String storeName,

        @Schema(description = "카테고리명", example = "음식")
        String categoryName,

        @Schema(description = "상품명", example = "1인럭키세트")
        String name,

        @Schema(description = "상품 가격", example = "28,000")
        int priceOriginal,

        @Schema(description = "할인가", example = "10,000")
        int priceDiscount,

        @Schema(description = "상품 수량", example = "1")
        int stock,

        @Schema(description = "픽업 시작 일시", example = "2025-06-15 08:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime pickupStartDateTime,

        @Schema(description = "픽업 종료 일시", example = "2025-06-15 13:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime pickupEndDateTime
) {
}
