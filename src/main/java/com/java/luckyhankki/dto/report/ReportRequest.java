package com.java.luckyhankki.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "신고 등록 요청 DTO")
public record ReportRequest(

        @Schema(description = "예약 ID", example = "1")
        @NotNull(message = "예약 ID는 필수입니다.")
        Long reservationId,

        @Schema(description = "신고 내용", example = "기재된 상품 설명과 달라 신고합니다.")
        @NotBlank(message = "신고 내용은 반드시 입력해야 합니다.")
        String content
) {
}
