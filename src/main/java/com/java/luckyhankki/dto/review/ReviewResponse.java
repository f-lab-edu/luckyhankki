package com.java.luckyhankki.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "리뷰 작성 후 응답 DTO")
public record ReviewResponse(

        @Schema(description = "리뷰 ID", example = "11")
        Long id,

        @Schema(description = "사용자 ID", example = "1")
        Long userId,

        @Schema(description = "상품 ID", example = "2")
        Long productId,

        @Schema(description = "키워드 목록", example = "음식이 맛있어요, 직원이 친절해요")
        List<String> keywords,

        @Schema(description = "별점(1~5)", example = "5")
        int ratingScore,

        @Schema(description = "리뷰 내용", example = "맛있게 잘 먹었습니다.")
        String content
) {
}
