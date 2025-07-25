package com.java.luckyhankki.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.luckyhankki.config.security.CustomUserDetails;
import com.java.luckyhankki.controller.ReviewController;
import com.java.luckyhankki.dto.review.ReviewListResponse;
import com.java.luckyhankki.dto.review.ReviewListResponse.ReviewDetailResponse;
import com.java.luckyhankki.dto.review.ReviewRequest;
import com.java.luckyhankki.dto.review.ReviewResponse;
import com.java.luckyhankki.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("리뷰 작성 웹 테스트")
    void addReview() throws Exception {
        Long userId = 1L;
        UsernamePasswordAuthenticationToken auth = getAuth(userId);

        ReviewRequest reviewRequest = new ReviewRequest(
                1L,
                List.of(1L, 2L),
                5,
                "맛있어요"
        );

        ReviewResponse reviewResponse = new ReviewResponse(
                1L,
                userId,
                1L,
                List.of("음식이 맛있어요", "픽업이 빨라요"),
                5,
                "맛있어요"
        );

        given(reviewService.addReview(eq(userId), any(ReviewRequest.class)))
                .willReturn(reviewResponse);

        mockMvc.perform(post("/reviews")
                        .content(objectMapper.writeValueAsString(reviewRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.keywords[0]").value("음식이 맛있어요"))
                .andExpect(jsonPath("$.keywords[1]").value("픽업이 빨라요"))
                .andExpect(jsonPath("$.ratingScore").value(5))
                .andExpect(jsonPath("$.content").value("맛있어요"));

        verify(reviewService).addReview(eq(userId), any(ReviewRequest.class));
    }

    @Test
    @DisplayName("사용자 리뷰 목록 조회")
    void getReviewsByUser() throws Exception {
        Long userId = 1L;
        UsernamePasswordAuthenticationToken auth = getAuth(userId);

        List<ReviewDetailResponse> reviewDetails = List.of(
                new ReviewDetailResponse(
                        10L, "비빔밥", List.of("음식이 맛있어요", "양이 많아요"), 5, "좋아요", LocalDateTime.now()
                )
        );
        ReviewListResponse response = new ReviewListResponse(1L, reviewDetails);

        given(reviewService.getReviewsByUser(eq(userId), any(Pageable.class))).willReturn(response);

        mockMvc.perform(get("/reviews")
                        .with(authentication(auth))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.reviewList[0].reviewId").value(10))
                .andExpect(jsonPath("$.reviewList[0].productName").value("비빔밥"))
                .andExpect(jsonPath("$.reviewList[0].keywords[0]").value("음식이 맛있어요"));

        verify(reviewService).getReviewsByUser(eq(userId), any(Pageable.class));
    }

    @Test
    @DisplayName("리뷰 삭제")
    void deleteReviewByUser() throws Exception {
        Long userId = 1L;
        Long reviewId = 5L;
        UsernamePasswordAuthenticationToken auth = getAuth(userId);

        mockMvc.perform(delete("/reviews/{reviewId}", reviewId)
                        .with(authentication(auth))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(reviewService).deleteReview(reviewId, userId);
    }

    private static UsernamePasswordAuthenticationToken getAuth(Long userId) {
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        given(userDetails.getUserId()).willReturn(userId);
        given(userDetails.getAuthorities()).willReturn(Collections.emptyList());

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}