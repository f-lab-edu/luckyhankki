package com.java.luckyhankki.dto.seller;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "판매자 회원가입 요청 DTO")
public record SellerRequest(

        @Schema(description = "사업자등록번호 (10자리 숫자)", example = "1234567890")
        @Pattern(
                regexp = "^[0-9]{10}$",
                message = "사업자등록번호는 10자리 숫자만 입력 가능합니다."
        )
        @NotBlank
        String businessNumber,

        @Schema(description = "대표자명", example = "홍길동", minimum = "2")
        @NotBlank(message = "대표자명은 반드시 입력해야 합니다.")
        @Size(min = 2)
        String name,

        @Schema(description = "비밀번호 (8~16자의 영문 대소문자, 숫자, 특수문자를 모두 포함)",
                minimum = "8",
                maximum = "16")
        @NotBlank
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+{}\\[\\]:;\"'<>,.?/~`\\\\|-]).{8,16}$",
                message = "비밀번호는 8~16자의 영문 대소문자, 숫자, 특수문자만 가능합니다."
        )
        String password,

        @Schema(description = "이메일", example = "test@test.com")
        @NotBlank
        @Email
        String email
) {
}
