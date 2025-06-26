package com.java.luckyhankki.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 결과 응답 DTO")
public record LoginResult(

        @Schema(description = "로그인 결과 상태", example = "success")
        String status,

        @Schema(description = "로그인 결과 메시지", example = "로그인 성공")
        String message,

        @Schema(description = "발급된 토큰 정보")
        String token
) {
}
