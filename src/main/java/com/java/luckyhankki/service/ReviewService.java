package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.keyword.Keyword;
import com.java.luckyhankki.domain.keyword.KeywordRepository;
import com.java.luckyhankki.domain.product.Product;
import com.java.luckyhankki.domain.product.ProductRepository;
import com.java.luckyhankki.domain.reservation.ReservationRepository;
import com.java.luckyhankki.domain.reservation.ReservationStatus;
import com.java.luckyhankki.domain.reservation.ReservationStatusProjection;
import com.java.luckyhankki.domain.review.Review;
import com.java.luckyhankki.domain.review.ReviewRepository;
import com.java.luckyhankki.dto.review.ReviewListResponse;
import com.java.luckyhankki.dto.review.ReviewListResponse.ReviewDetailResponse;
import com.java.luckyhankki.dto.review.ReviewRequest;
import com.java.luckyhankki.dto.review.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final KeywordRepository keywordRepository;
    private final ProductRepository productRepository;
    private final ReservationRepository reservationRepository;

    public ReviewService(ReviewRepository reviewRepository, KeywordRepository keywordRepository, ProductRepository productRepository, ReservationRepository reservationRepository) {
        this.reviewRepository = reviewRepository;
        this.keywordRepository = keywordRepository;
        this.productRepository = productRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public ReviewResponse addReview(Long userId, ReviewRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        ReservationStatus status = reservationRepository.findByUserIdAndProductId(userId, request.productId())
                .map(ReservationStatusProjection::getStatus)
                .orElseThrow(() -> new RuntimeException("예약 건을 찾을 수 없습니다."));

        if (status != ReservationStatus.COMPLETED) {
            throw new RuntimeException("픽업이 완료된 후에 리뷰를 작성할 수 있습니다.");
        }

        boolean exists = reviewRepository.existsReviewByUserIdAndProductId(userId, request.productId());
        if (exists) {
            throw new RuntimeException("이미 작성된 예약 건입니다.");
        }

        List<Keyword> keywords = keywordRepository.findAllById(request.keywordIds());

        Review review = new Review(userId, product, keywords, request.ratingScore(), request.content());
        Review savedReview = reviewRepository.save(review);

        return new ReviewResponse(
                savedReview.getId(),
                savedReview.getUserId(),
                savedReview.getProduct().getId(),
                savedReview.getKeywords().stream().map(Keyword::getKeyword).toList(),
                savedReview.getRatingScore(),
                savedReview.getContent());
    }

    @Transactional(readOnly = true)
    public ReviewListResponse getReviewsByUser(Long userId, Pageable pageable) {
        Page<Review> reviewPage = reviewRepository.findAllByUserId(userId, pageable);

        List<ReviewDetailResponse> reviewList = reviewPage.getContent().stream()
                .map(review -> new ReviewDetailResponse(
                        review.getId(),
                        review.getProduct().getName(),
                        review.getKeywords().stream().map(Keyword::getKeyword).toList(),
                        review.getRatingScore(),
                        review.getContent(),
                        review.getCreatedAt()))
                .toList();

        return new ReviewListResponse(reviewPage.getTotalElements(), reviewList);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        reviewRepository.deleteByIdAndUserId(reviewId, userId);
    }
}
