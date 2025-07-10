package com.java.luckyhankki.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리자 전용 가게 및 판매자 조회 응답 DTO")
public record AdminStoreWithSellerResponse(

        @Schema(description = "관리자 전용 가게 조회 응답 DTO")
        AdminStoreResponse adminStoreResponse,

        @Schema(description = "관리자 전용 판매자 조회 응답 DTO")
        AdminSellerResponse adminSellerResponse
) {
}
