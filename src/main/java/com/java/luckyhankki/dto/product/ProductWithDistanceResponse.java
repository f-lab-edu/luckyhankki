package com.java.luckyhankki.dto.product;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "상품 목록 조회 응답 DTO (거리 정보 포함)")
@QueryProjection
public record ProductWithDistanceResponse(

        @JsonUnwrapped
        @Schema(description = "상품 정보")
        ProductResponse product,

        @Schema(description = "사용자와 가게 간 거리 (km)", example = "2.5")
        BigDecimal distance
) {
}