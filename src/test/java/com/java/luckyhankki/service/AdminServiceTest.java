package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.seller.Seller;
import com.java.luckyhankki.domain.seller.SellerRepository;
import com.java.luckyhankki.domain.store.Store;
import com.java.luckyhankki.domain.store.StoreRepository;
import com.java.luckyhankki.dto.admin.AdminStoreResponse;
import com.java.luckyhankki.dto.admin.AdminStoreWithSellerResponse;
import com.java.luckyhankki.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private SellerRepository sellerRepository;

    @Test
    @DisplayName("등록된 모든 가게 조회")
    void findAllStore() {
        Store store1 = mock(Store.class);
        Store store2 = mock(Store.class);

        when(storeRepository.findAll()).thenReturn(List.of(store1, store2));

        List<AdminStoreResponse> result = adminService.findAllStore();

        assertThat(result).hasSize(2);

        verify(storeRepository).findAll();
    }

    @Test
    @DisplayName("판매자 정보 포함 가게 조회")
    void findStoreByStoreId() {
        Seller seller = mock(Seller.class);
        when(seller.getBusinessNumber()).thenReturn("1234567890");

        long storeId = 1L;
        Store store = new Store(seller, "길동카페", "021231234", "경기도 수원시", BigDecimal.valueOf(36.123123), BigDecimal.valueOf(126.123123));

        when(storeRepository.findStoreAndSellerById(storeId)).thenReturn(store);

        AdminStoreWithSellerResponse response = adminService.findStoreByStoreId(storeId);

        assertThat(response.adminSellerResponse().businessNumber()).isEqualTo("1234567890");
        assertThat(response.adminStoreResponse().name()).isEqualTo("길동카페");

        verify(storeRepository).findStoreAndSellerById(storeId);
    }

    @Test
    @DisplayName("가게 승인 성공")
    void approveStore_success() {
        Seller seller = mock(Seller.class);
        when(seller.getBusinessNumber()).thenReturn("1234567890");

        long storeId = 1L;
        Store store = new Store(
                seller,
                "길동카페",
                "021231234",
                "경기도 수원시",
                BigDecimal.valueOf(126.123123),
                BigDecimal.valueOf(36.123123));

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        assertThat(store.isApproved()).isFalse();

        adminService.approveStore(storeId);
        assertThat(store.isApproved()).isTrue();

        verify(storeRepository).findById(storeId);
    }

    @Test
    @DisplayName("가게 승인 실패 - 가게 정보 찾을 수 없음")
    void approveStore_throwsException_whenStoreNotFound() {
        long storeId = 99L;

        when(storeRepository.findById(storeId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> adminService.approveStore(storeId))
                .isInstanceOf(CustomException.class);

        verify(storeRepository).findById(99L);
    }
}