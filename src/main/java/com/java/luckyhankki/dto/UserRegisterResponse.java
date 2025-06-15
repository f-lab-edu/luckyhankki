package com.java.luckyhankki.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "회원가입 응답 DTO")
public record UserRegisterResponse(

        @Schema(description = "회원 테이블의 ID", example = "1")
        Long userId,

        @Schema(description = "회원명", example = "홍길동")
        String name,

        @Schema(description = "ID로 사용될 이메일", example = "test@test.com")
        String email,

        @Schema(description = "회원 타입", example = "CUSTOMER")
        String roleType,

        @Schema(description = "회원가입 일시", example = "2025-06-15 09:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime createdAt
) {
}
