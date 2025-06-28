package com.java.luckyhankki.dto.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "상품 상세 조회 응답 DTO")
public record ProductDetailResponse(

        @Schema(description = "가게명", example = "길동국밥")
        String storeName,

        @Schema(description = "가게 주소", example = "경기도 수원시 XX구 XX동")
        String storeAddress,

        @Schema(description = "가게 전화번호", example = "031-123-4567")
        String storePhone,

        @Schema(description = "카테고리명", example = "음식")
        String categoryName,

        @Schema(description = "상품명", example = "1인럭키세트")
        String productName,

        @Schema(description = "상품 설명", example = "길동국밥의 시그니처 국밥과 럭키반찬들로 이루어진 구성품입니다.")
        String description,

        @Schema(description = "상품 수량", example = "1")
        int stock,

        @Schema(description = "상품 가격", example = "28,000")
        int priceOriginal,

        @Schema(description = "할인가", example = "10,000")
        int priceDiscount,

        @Schema(description = "픽업 시작 일시", example = "2025-06-15 08:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime pickupStartDateTime,

        @Schema(description = "픽업 종료 일시", example = "2025-06-15 13:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime pickupEndDateTime
        //TODO 평점, 별점, 키워드
) {
}
