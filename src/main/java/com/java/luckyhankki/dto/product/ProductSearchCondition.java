package com.java.luckyhankki.dto.product;

import com.querydsl.core.types.Order;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 조회 조건 DTO")
public record ProductSearchCondition(

        @Schema(description = "카테고리 ID", example = "1")
        Long categoryId,

        @Schema(description = "픽업 날짜(NOW, TODAY, TOMORROW)", example = "TODAY")
        PickupDateFilter pickupDate,

        @Schema(description = "검색 키워드", example = "빵")
        String keyword,

        @Schema(description = "정렬 방식(DISTANCE, PRICE, RATING)", example = "PRICE")
        SortType sortType
) {
    public enum PickupDateFilter {
        NOW, TODAY, TOMORROW
    }

    public enum SortType {
        DISTANCE, PRICE, RATING;

        public Order getOrder() {
            return switch(this) {
                case DISTANCE, PRICE -> Order.ASC;
                case RATING -> Order.DESC;
            };
        }
    }
}
