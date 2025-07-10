package com.java.luckyhankki.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "상품 조회 요청 DTO(상품 조회 조건 DTO 포함)")
public record ProductSearchRequest(

        @Schema(description = "상품 조회 조건 요청 DTO")
        @NotNull
        ProductSearchCondition condition,

        @Schema(description = "사용자 위도", example = "37.586671")
        @NotNull
        double userLat,

        @Schema(description = "사용자 경도", example = "126.976112")
        @NotNull
        double userLon
) {
}
