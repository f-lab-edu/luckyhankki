package com.java.luckyhankki.dto.admin;

import com.java.luckyhankki.domain.report.ReportStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "신고 처리 응답 DTO")
public record AdminReportHandleResponse(

        @Schema(description = "신고 ID", example = "1")
        Long id,

        @Schema(description = "신고 처리 상태", example = "RESOLVED")
        ReportStatus status,

        @Schema(description = "처리 내용 메모", example = "처리되었습니다.")
        String adminMemo,

        @Schema(description = "가게 ID", example = "1")
        Long storeId,

        @Schema(description = "가게 신고 누적 횟수", example = "2")
        int reportCount
) {
}
