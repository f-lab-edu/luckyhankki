package com.java.luckyhankki.dto.keyword;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "키워드 조회 응답 DTO")
public record KeywordResponse(

        @Schema(description = "키워드 ID", example = "1")
        Long id,

        @Schema(description = "키워드", example = "음식이 맛있어요")
        String keyword
) {
}
