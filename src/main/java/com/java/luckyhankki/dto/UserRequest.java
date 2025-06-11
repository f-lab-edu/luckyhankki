package com.java.luckyhankki.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UserRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+{}\\[\\]:;\"'<>,.?/~`\\\\|-]).{8,16}$",
                message = "비밀번호는 8~16자의 영문 대소문자, 숫자, 특수문자만 가능합니다."
        )
        String password,

        @NotBlank
        @Size(min = 2)
        String name,

        @NotBlank
        String phone,

        @NotBlank
        String address,

        @NotNull(message = "소수점 이하 6자리까지의 경도를 입력하세요.")
        BigDecimal longitude,

        @NotNull(message = "소수점 이하 6자리까지의 경도를 입력하세요.")
        BigDecimal latitude
) {
}
