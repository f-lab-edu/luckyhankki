package com.java.luckyhankki.dto.common;

import com.java.luckyhankki.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Schema(description = "Exception 응답 DTO")
public record ErrorResponse(
        @Schema(description = "에러 코드", example = "SELLER_NOT_FOUND")
        String code,

        @Schema(description = "에러 메시지", example = "존재하지 않는 판매자입니다.")
        String message,

        @Schema(description = "에러 발생 시간", example = "2025-07-01 15:47:49")
        String timestamp,

        @Schema(description = "에러 상세 메시지 목록", example = "[\"Field 'priceOriginal': 가격은 10원 단위어야 합니다.\"]")
        List<String> details
) {
    public ErrorResponse(ErrorCode errorCode) {
        this(errorCode, List.of());
    }

    public ErrorResponse(ErrorCode errorCode, List<String> details) {
        this(errorCode.getCode(),
            errorCode.getMessage(),
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            details != null ? details : List.of());
    }
}
