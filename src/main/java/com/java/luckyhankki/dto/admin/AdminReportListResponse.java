package com.java.luckyhankki.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "신고 목록 조회 응답 DTO")
public record AdminReportListResponse(

        @Schema(description = "신고 ID", example = "1")
        Long id,

        @Schema(description = "예약 ID", example = "1")
        Long reservationId,

        @Schema(description = "사용자 ID", example = "1")
        Long userId,

        @Schema(description = "신고 일시", example = "2025-06-15 08:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime createdAt
) {
}
