package com.java.luckyhankki.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "리뷰 작성 요청 DTO")
public record ReviewRequest(

        @Schema(description = "상품 ID", example = "1")
        @NotNull
        Long productId,

        @Schema(description = "키워드 리스트")
        @NotNull
        List<Long> keywordIds,

        @Schema(description = "별점(1~5)", example = "5")
        @NotNull
        @Min(1)
        @Max(5)
        int ratingScore,

        @Schema(description = "리뷰 내용", example = "맛있게 잘 먹었습니다.")
        @Nullable
        String content
) {
}
