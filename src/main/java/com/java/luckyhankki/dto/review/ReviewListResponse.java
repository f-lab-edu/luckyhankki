package com.java.luckyhankki.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "리뷰 목록 응답 DTO")
public record ReviewListResponse(

        @Schema(description = "총 리뷰 수", example = "10")
        long totalCount,

        @Schema(description = "리뷰 상세 응답 DTO")
        List<ReviewDetailResponse> reviewList
) {
    @Schema(description = "리뷰 상세 응답 DTO")
    public record ReviewDetailResponse(

            @Schema(description = "리뷰 ID", example = "11")
            Long reviewId,

            @Schema(description = "상품명", example = "1인럭키세트")
            String productName,

            @Schema(description = "키워드 목록", example = "음식이 맛있어요, 직원이 친절해요")
            List<String> keywords,

            @Schema(description = "별점(1~5)", example = "5")
            int ratingScore,

            @Schema(description = "리뷰 내용", example = "맛있게 잘 먹었습니다.")
            String content,

            @Schema(description = "리뷰 작성 날짜")
            LocalDateTime createdAt
    ) {}
}
