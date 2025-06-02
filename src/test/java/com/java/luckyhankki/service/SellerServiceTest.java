package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.seller.Seller;
import com.java.luckyhankki.domain.seller.SellerProjection;
import com.java.luckyhankki.domain.seller.SellerRepository;
import com.java.luckyhankki.dto.SellerRequest;
import com.java.luckyhankki.dto.SellerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SellerServiceTest {

    private SellerRepository sellerRepository = mock(SellerRepository.class);
    private SellerService sellerService;

    @BeforeEach
    void setUp() {
        sellerService = new SellerService(sellerRepository);
    }

    @Test
    @DisplayName("판매자 회원가입 성공")
    void join_success() {
        // given
        SellerRequest request = new SellerRequest(
                "1234567890", "홍길동", "admin1234", "test@test.com"
        );

        when(sellerRepository.save(any(Seller.class)))
                .then(returnsFirstArg());

        // when
        SellerResponse result = sellerService.join(request);

        // then
        assertEquals("1234567890", result.businessNumber());
        assertEquals("홍길동", result.name());
        assertEquals("test@test.com", result.email());

        verify(sellerRepository).save(any());
    }

    @Test
    @DisplayName("사업자등록번호 중복 시 예외 발생")
    void join_throwsException_whenBusinessNumberExists() {
        // given
        SellerRequest request = new SellerRequest(
                "1234567890", "홍길동", "admin1234", "test@test.com"
        );

        // 중복된 사업자등록번호라고 가정
        when(sellerRepository.existsByBusinessNumber("1234567890")).thenReturn(true);

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            sellerService.join(request);
        });

        assertEquals("이미 존재하는 사업자등록번호입니다.", exception.getMessage());

        verify(sellerRepository).existsByBusinessNumber(any());
    }

    @Test
    @DisplayName("존재하는 사업자등록번호로 판매자 조회하기")
    void findSellerByBusinessNumber_success() {
        //given
        String businessNumber = "1234567890";
        SellerProjection projection = mock(SellerProjection.class);
        when(projection.getName()).thenReturn("홍길동");
        when(projection.getEmail()).thenReturn("test@test.com");

        when(sellerRepository.findByBusinessNumber(businessNumber))
                .thenReturn(Optional.of(projection));

        //when
        SellerResponse result = sellerService.findSellerByBusinessNumber(businessNumber);

        //then
        assertEquals(businessNumber, result.businessNumber());
        assertEquals("홍길동", result.name());
        assertEquals("test@test.com", result.email());

        verify(sellerRepository).findByBusinessNumber(businessNumber);
    }

    @Test
    @DisplayName("존재하지 않는 판매자 조회 시 예외 발생")
    void findSellerByBusinessNumber_throwsException_whenSellerNotExists() {
        // given
        String businessNumber = "0000000000";

        when(sellerRepository.findByBusinessNumber(businessNumber))
                .thenReturn(Optional.empty());

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            sellerService.findSellerByBusinessNumber(businessNumber);
        });

        assertEquals("존재하지 않는 판매자입니다.", exception.getMessage());

        verify(sellerRepository).findByBusinessNumber(businessNumber);
    }
}