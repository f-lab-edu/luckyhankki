package com.java.luckyhankki.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리자 전용 판매자 조회 응답 DTO")
public record AdminSellerResponse(

        @Schema(description = "판매자 ID", example = "1")
        long id,

        @Schema(description = "사업자등록번호", example = "1234567890")
        String businessNumber,

        @Schema(description = "판매자명", example = "홍길동")
        String sellerName,

        @Schema(description = "판매자 이메일", example = "test@test.com")
        String email
) {
}
