package com.java.luckyhankki.dto.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.java.luckyhankki.domain.report.ReportStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "신고 목록 응답 DTO")
public record ReportListResponse(

        @Schema(description = "신고 ID", example = "1")
        Long id,

        @Schema(description = "예약 ID", example = "1")
        Long reservationId,

        @Schema(description = "신고 처리 여부", example = "true")
        boolean isCompleted,

        @Schema(description = "신고 처리 상태", example = "RESOLVED")
        ReportStatus status,

        @Schema(description = "처리 내용 메모", example = "처리되었습니다.")
        String adminMemo,

        @Schema(description = "신고 일시", example = "2025-06-15 08:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime createdAt
) {
}
