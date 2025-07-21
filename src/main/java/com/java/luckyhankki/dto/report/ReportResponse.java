package com.java.luckyhankki.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "신고 후 응답 DTO")
public record ReportResponse(

        @Schema(description = "신고 ID", example = "11")
        Long id,

        @Schema(description = "사용자 ID", example = "1")
        Long userId,

        @Schema(description = "예약 ID", example = "1")
        Long reservationId,

        @Schema(description = "신고 내용", example = "기재된 상품 설명과 달라 신고합니다.")
        String content
) {
}
