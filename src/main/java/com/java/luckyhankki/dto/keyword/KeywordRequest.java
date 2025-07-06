package com.java.luckyhankki.dto.keyword;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "리뷰 키워드 등록/수정 요청 DTO")
public record KeywordRequest(

        @Schema(description = "키워드", example = "음식이 맛있어요")
        @NotBlank(message = "키워드는 반드시 입력해야 합니다.")
        String keyword
) {
}
