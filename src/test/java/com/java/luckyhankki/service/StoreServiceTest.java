package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.seller.Seller;
import com.java.luckyhankki.domain.seller.SellerRepository;
import com.java.luckyhankki.domain.store.Store;
import com.java.luckyhankki.domain.store.StoreRepository;
import com.java.luckyhankki.dto.StoreRequest;
import com.java.luckyhankki.dto.StoreResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StoreServiceTest {

    private final SellerRepository sellerRepository = Mockito.mock(SellerRepository.class);
    private final StoreRepository storeRepository = Mockito.mock(StoreRepository.class);
    private StoreService storeService;

    @BeforeEach
    void setUp() {
        storeService = new StoreService(sellerRepository, storeRepository);
    }

    @Test
    @DisplayName("가게 등록 성공")
    void registerStore_success() {
        //given
        Long sellerId = 1L;
        Seller seller = Seller.create("1234567890", "홍길동", "password123", "test@example.com");

        StoreRequest storeRequest = new StoreRequest(
                "가게명1",
                "02-1234-5678",
                "서울특별시 종로구 청와대로 1",
                BigDecimal.valueOf(126.978414),
                BigDecimal.valueOf(37.566680)
        );

        when(sellerRepository.findById(sellerId))
                .thenReturn(Optional.of(seller));

        when(storeRepository.save(any(Store.class)))
                .thenAnswer(returnsFirstArg());

        //when
        StoreResponse storeResponse = storeService.registerStore(sellerId, storeRequest);

        //then
        assertEquals("가게명1", storeResponse.name());
        verify(sellerRepository).findById(sellerId);
        verify(storeRepository).save(any(Store.class));
    }

    @Test
    @DisplayName("이미 등록된 가게가 있을 경우 가게 등록 실패")
    void registerStore_throwsException_whenStoreAlreadyExists() {
        Long sellerId = 1L;
        StoreRequest storeRequest = new StoreRequest(
                "가게명1",
                "02-1234-5678",
                "서울특별시 종로구 청와대로 1",
                BigDecimal.valueOf(126.978414),
                BigDecimal.valueOf(37.566680)
        );

        Seller seller = Seller.create("1234567890", "홍길동", "password123", "test@example.com");

        when(sellerRepository.findById(sellerId))
                .thenReturn(Optional.of(seller));

        when(storeRepository.existsStoreBySellerId(sellerId))
                .thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> storeService.registerStore(sellerId, storeRequest));

        assertEquals("이미 등록된 가게가 있습니다.", exception.getMessage());
    }
}