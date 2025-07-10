package com.java.luckyhankki.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "관리자 전용 가게 조회 응답 DTO")
public record AdminStoreResponse(

        @Schema(description = "가게 ID", example = "1")
        Long id,

        @Schema(description = "가게명", example = "길동국밥")
        String name,

        @Schema(description = "가게 전화번호", example = "03112341234")
        String phone,

        @Schema(description = "가게 주소", example = "경기도 수원시 XX구 XX대로")
        String address,

        @Schema(description = "가게 승인 여부", example = "true")
        boolean isApproved,

        @Schema(description = "신고 누적 수", example = "0")
        int reportCount,

        @Schema(description = "가게 등록 일시", example = "2025-07-07 17:30:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime createdAt
) {
}
