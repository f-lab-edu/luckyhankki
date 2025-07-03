package com.java.luckyhankki.dto.store;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "가게 등록 응답 DTO")
public record StoreResponse(

        @Schema(description = "가게 ID", example = "1")
        Long id,

        @Schema(description = "가게명", example = "길동국밥")
        String name,

        @Schema(description = "가게 번호", example = "031-123-4567")
        String phone,

        @Schema(description = "가게 주소", example = "경기도 수원시 XX구 XX동")
        String address,

        @Schema(description = "가게 승인 여부", example = "true")
        boolean isApproved,

        @Schema(description = "가게 신고 횟수", example = "0")
        int reportCount
) {
}
