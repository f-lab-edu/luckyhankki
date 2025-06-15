package com.java.luckyhankki.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "판매자 회원가입 응답 DTO")
public record SellerResponse(

        @Schema(description = "사업자등록번호 (10자리 숫자)", example = "1234567890")
        String businessNumber,

        @Schema(description = "대표자명", example = "홍길동")
        String name,

        @Schema(description = "이메일", example = "test@test.com")
        String email
) {
}
