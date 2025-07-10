package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.keyword.Keyword;
import com.java.luckyhankki.domain.keyword.KeywordRepository;
import com.java.luckyhankki.domain.product.Product;
import com.java.luckyhankki.domain.product.ProductRepository;
import com.java.luckyhankki.domain.reservation.ReservationRepository;
import com.java.luckyhankki.domain.reservation.ReservationStatus;
import com.java.luckyhankki.domain.review.Review;
import com.java.luckyhankki.domain.review.ReviewRepository;
import com.java.luckyhankki.dto.review.ReviewListResponse;
import com.java.luckyhankki.dto.review.ReviewRequest;
import com.java.luckyhankki.dto.review.ReviewResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private KeywordRepository keywordRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    @DisplayName("리뷰 저장 성공 테스트")
    void addReview_success() {
        long userId = 1L;
        ReviewRequest reviewRequest = new ReviewRequest(1L, List.of(1L), 5, "맛있어요");
        Product product = Mockito.mock(Product.class);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(reservationRepository.findByUserIdAndProductId(userId, 1L))
                .thenReturn(() -> ReservationStatus.COMPLETED);

        when(reviewRepository.save(any(Review.class)))
                .then(returnsFirstArg());

        ReviewResponse reviewResponse = reviewService.addReview(userId, reviewRequest);

        assertEquals(5, reviewResponse.ratingScore());
        assertEquals("맛있어요", reviewResponse.content());

        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    @DisplayName("예약 상태가 COMPLETED가 아닐 때 예외 발생")
    void addReview_throwsException_whenReservationStatusIsNotCompleted() {
        long userId = 1L;
        ReviewRequest reviewRequest = new ReviewRequest(1L, List.of(1L), 5, "맛있어요");
        Product product = Mockito.mock(Product.class);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(reservationRepository.findByUserIdAndProductId(userId, 1L))
                .thenReturn(() -> ReservationStatus.PENDING);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> reviewService.addReview(userId, reviewRequest));

        assertEquals("픽업이 완료된 후에 리뷰를 작성할 수 있습니다.", exception.getMessage());

        verify(reservationRepository).findByUserIdAndProductId(userId, 1L);
    }

    @Test
    @DisplayName("사용자 리뷰 목록 조회 성공 테스트")
    void getReviewsByUser_success() {
        long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        Product product = Mockito.mock(Product.class);
        Keyword keyword = Mockito.mock(Keyword.class);

        Review review = new Review(userId, product, List.of(keyword), 5, "맛있어요");

        Page<Review> reviewPage = new PageImpl<>(List.of(review), pageable, 1);

        when(reviewRepository.findAllByUserId(userId, pageable))
                .thenReturn(reviewPage);

        ReviewListResponse result = reviewService.getReviewsByUser(userId, pageable);

        assertEquals(1, result.totalCount());
        assertEquals(1, result.reviewList().size());
        assertEquals(5, result.reviewList().get(0).ratingScore());

        verify(reviewRepository).findAllByUserId(userId, pageable);
    }

    @Test
    @DisplayName("리뷰 삭제 테스트")
    void deleteReview_success() {
        long reviewId = 1L;
        long userId = 1L;

        willDoNothing().given(reviewRepository).deleteByIdAndUserId(reviewId, userId);

        reviewService.deleteReview(userId, reviewId);

        verify(reviewRepository).deleteByIdAndUserId(reviewId, userId);
    }
}
