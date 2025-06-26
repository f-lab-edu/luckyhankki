package com.java.luckyhankki.dto.seller;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "판매자 로그인 요청 DTO")
public record SellerLoginRequest(

        @Schema(description = "사업자등록번호(ID)")
        @NotBlank
        String businessNumber,

        @Schema(description = "비밀번호")
        @NotBlank
        String password
) {
}
