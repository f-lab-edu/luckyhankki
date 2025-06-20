package com.java.luckyhankki.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "회원가입 요청 DTO")
public record UserRequest(

        @Schema(description = "ID로 사용될 이메일", example = "test@test.com")
        @NotBlank
        @Email
        String email,

        @Schema(description = "비밀번호 (8~16자의 영문 대소문자, 숫자, 특수문자를 모두 포함)",
                minimum = "8",
                maximum = "16")
        @NotBlank
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+{}\\[\\]:;\"'<>,.?/~`\\\\|-]).{8,16}$",
                message = "비밀번호는 8~16자의 영문 대소문자, 숫자, 특수문자만 가능합니다."
        )
        String password,

        @Schema(description = "회원명", minimum = "2", example = "홍길동")
        @NotBlank
        @Size(min = 2)
        String name,

        @Schema(description = "휴대폰 번호", example = "010-1234-5678")
        @NotBlank
        String phone,

        @Schema(description = "주소", example = "경기도 수원시 XX구 XX동")
        @NotBlank
        String address,

        @Schema(description = "경도 (소수점 이하 6자리)", example = "126.123456")
        @NotNull(message = "소수점 이하 6자리까지의 경도를 입력하세요.")
        BigDecimal longitude,

        @Schema(description = "위도 (소수점 이하 6자리)", example = "36.123456")
        @NotNull(message = "소수점 이하 6자리까지의 경도를 입력하세요.")
        BigDecimal latitude
) {
}
