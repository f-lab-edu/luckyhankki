package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.seller.Seller;
import com.java.luckyhankki.domain.seller.SellerRepository;
import com.java.luckyhankki.domain.store.Store;
import com.java.luckyhankki.domain.store.StoreRepository;
import com.java.luckyhankki.dto.StoreRequest;
import com.java.luckyhankki.dto.StoreResponse;
import com.java.luckyhankki.dto.StoreUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
        Seller seller = getSeller();
        StoreRequest storeRequest = getStoreRequest();

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
        StoreRequest storeRequest = getStoreRequest();
        Seller seller = getSeller();

        when(sellerRepository.findById(sellerId))
                .thenReturn(Optional.of(seller));

        when(storeRepository.existsStoreBySellerId(sellerId))
                .thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> storeService.registerStore(sellerId, storeRequest));

        assertEquals("이미 등록된 가게가 있습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("가게 정보 전체 수정 성공")
    void updateStore_fullUpdate_success() {
        Long storeId = 1L;
        Store store = getStore();

        when(storeRepository.findById(storeId))
                .thenReturn(Optional.of(store));

        StoreUpdateRequest updateRequest = new StoreUpdateRequest(
                "가게명2",
                "031-9999-9999",
                "경기도 수원시",
                BigDecimal.valueOf(127.123456),
                BigDecimal.valueOf(37.123456)
        );

        storeService.updateStore(storeId, updateRequest);

        assertEquals("가게명2", store.getName());
        assertEquals("031-9999-9999", store.getPhone());
        assertEquals("경기도 수원시", store.getAddress());
        assertEquals(BigDecimal.valueOf(127.123456), store.getLongitude());
        assertEquals(BigDecimal.valueOf(37.123456), store.getLatitude());
    }

    @Test
    @DisplayName("가게 정보 일부 수정 성공")
    void updateStore_partialUpdate_success() {
        Long storeId = 1L;
        Store store = getStore();

        when(storeRepository.findById(storeId))
                .thenReturn(Optional.of(store));

        StoreUpdateRequest updateRequest = new StoreUpdateRequest(
                "가게명2",
                null,
                null,
                null,
                null
        );

        storeService.updateStore(storeId, updateRequest);

        assertEquals("가게명2", store.getName());
        assertEquals("02-1234-5678", store.getPhone());
        assertEquals("서울특별시 종로구 청와대로 1", store.getAddress());
        assertEquals(BigDecimal.valueOf(126.978414), store.getLongitude());
        assertEquals(BigDecimal.valueOf(37.566680), store.getLatitude());
    }

    @Test
    @DisplayName("가게 soft delete")
    void deleteStore_success() {
        Long storeId = 1L;
        Store store = getStore();

        when(storeRepository.findById(storeId))
                .thenReturn(Optional.of(store));

        storeService.deleteStore(storeId);

        assertTrue(store.isDeleted());
        verify(storeRepository).findById(storeId);
        verify(storeRepository).deleteById(storeId);
    }

    private static StoreRequest getStoreRequest() {
        return new StoreRequest(
                "가게명1",
                "02-1234-5678",
                "서울특별시 종로구 청와대로 1",
                BigDecimal.valueOf(126.978414),
                BigDecimal.valueOf(37.566680)
        );
    }

    private static Seller getSeller() {
        return Seller.create("1234567890", "홍길동", "password123", "test@example.com");
    }

    private static Store getStore() {
        StoreRequest request = getStoreRequest();
        return Store.create(getSeller(), request.name(), request.phone(), request.address(), request.longitude(), request.latitude());
    }
}