package com.java.luckyhankki.dto.admin;

import com.java.luckyhankki.domain.report.ReportStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "신고 처리 요청 DTO")
public record AdminReportHandleRequest(

        @Schema(description = "신고 처리 상태", example = "RESOLVED")
        @NotNull
        ReportStatus reportStatus,

        @Schema(description = "신고 처리 내용", example = "처리되었습니다.")
        @NotBlank
        String adminMemo
) {
}
