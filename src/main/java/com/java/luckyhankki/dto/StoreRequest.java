package com.java.luckyhankki.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record StoreRequest(
        @NotBlank(message = "가게명은 반드시 입력해야 합니다.")
        String name,

        @NotBlank(message = "가게 번호는 반드시 입력해야 합니다.")
        String phone,

        @NotBlank(message = "가게 주소는 반드시 입력해야 합니다.")
        String address,

        @NotNull(message = "소수점 이하 6자리까지의 경도를 입력하세요.")
        BigDecimal longitude,

        @NotNull(message = "소수점 이하 6자리까지의 위도를 입력하세요.")
        BigDecimal latitude
) {
}
