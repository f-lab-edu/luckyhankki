package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.seller.Seller;
import com.java.luckyhankki.domain.seller.SellerRepository;
import com.java.luckyhankki.dto.SellerRequest;
import com.java.luckyhankki.dto.SellerResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SellerService sellerService;

    @Test
    @DisplayName("판매자 회원가입 성공")
    void join_success() {
        // given
        SellerRequest request = new SellerRequest(
                "1234567890", "홍길동", "admin1234", "test@test.com"
        );

        //any() : 어떤 Seller 객체든 상관X
        when(sellerRepository.save(any(Seller.class)))
                .then(returnsFirstArg());

        // when
        SellerResponse responseDto = sellerService.join(request);

        // then
        assertEquals("1234567890", responseDto.businessNumber());
        assertEquals("홍길동", responseDto.name());
        assertEquals("test@test.com", responseDto.email());

        //verify(): 테스트 대상 코드가 특정 메서드를 실제로 호출했는지 확인하는 데 사용
        verify(sellerRepository).save(any());
    }

    @Test
    @DisplayName("사업자등록번호 중복 시 예외 발생")
    void join_throwsException_whenBusinessNumberExists() {
        // given
        SellerRequest requestDTO = new SellerRequest(
                "1234567890", "홍길동", "admin1234", "test@test.com"
        );

        // 중복된 사업자등록번호라고 가정
        when(sellerRepository.existsByBusinessNumber("1234567890")).thenReturn(true);

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            sellerService.join(requestDTO);
        });

        assertEquals("이미 존재하는 사업자등록번호입니다.", exception.getMessage());

        verify(sellerRepository).existsByBusinessNumber(any());
    }
}