package com.java.luckyhankki.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "사용자 로그인 요청 DTO")
public record UserLoginRequest(

        @Schema(description = "이메일(ID)")
        @NotBlank
        @Email String email,

        @Schema(description = "비밀번호")
        @NotBlank
        String password
) {
}
