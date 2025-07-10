package com.java.luckyhankki.controller;

import com.java.luckyhankki.config.security.CustomUserDetails;
import com.java.luckyhankki.dto.review.ReviewListResponse;
import com.java.luckyhankki.dto.review.ReviewRequest;
import com.java.luckyhankki.dto.review.ReviewResponse;
import com.java.luckyhankki.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Review", description = "예약 관련 API")
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(summary = "리뷰 등록", description = "구매자가 상품에 대한 리뷰를 등록합니다.")
    @PostMapping
    public ResponseEntity<ReviewResponse> addReview(
            @AuthenticationPrincipal CustomUserDetails userDetail,
            @Valid @RequestBody ReviewRequest request) {

        ReviewResponse review = reviewService.addReview(userDetail.getUserId(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    @Operation(summary = "리뷰 목록 조회", description = "사용자가 작성한 모든 리뷰를 조회합니다.")
    @GetMapping
    public ResponseEntity<ReviewListResponse> getReviewsByUser(
            @AuthenticationPrincipal CustomUserDetails userDetail,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Long userId = userDetail.getUserId();
        ReviewListResponse reviews = reviewService.getReviewsByUser(userId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(reviews);
    }

    @Operation(summary = "리뷰 삭제", description = "리뷰 ID에 해당하는 리뷰를 삭제합니다.")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReviewByUser(
            @Parameter(description = "리뷰 ID") @PathVariable("reviewId") Long reviewId,
            @AuthenticationPrincipal CustomUserDetails userDetail) {

        reviewService.deleteReview(reviewId, userDetail.getUserId());

        return ResponseEntity.noContent().build();
    }
}
