package com.java.luckyhankki.dto.store;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "가게 등록 요청 DTO")
public record StoreRequest(

        @Schema(description = "가게명", example = "길동국밥")
        @NotBlank(message = "가게명은 반드시 입력해야 합니다.")
        String name,

        @Schema(description = "가게 번호", example = "031-123-4567")
        @NotBlank(message = "가게 번호는 반드시 입력해야 합니다.")
        String phone,

        @Schema(description = "가게 주소", example = "경기도 수원시 XX구 XX동")
        @NotBlank(message = "가게 주소는 반드시 입력해야 합니다.")
        String address,

        @Schema(description = "경도 (소수점 이하 6자리)", example = "126.123456")
        @NotNull(message = "소수점 이하 6자리까지의 경도를 입력하세요.")
        BigDecimal longitude,

        @Schema(description = "위도 (소수점 이하 6자리)", example = "36.123456")
        @NotNull(message = "소수점 이하 6자리까지의 경도를 입력하세요.")
        BigDecimal latitude
) {
}
