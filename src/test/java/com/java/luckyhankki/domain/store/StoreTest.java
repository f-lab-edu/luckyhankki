package com.java.luckyhankki.domain.store;

import com.java.luckyhankki.domain.seller.Seller;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class StoreTest {

    @Autowired
    private StoreRepository storeRepository;

    @Test
    @DisplayName("가게 등록 성공")
    void save() {
        Seller seller = new Seller("1234567890", "판매자1", "password123", "seller@test.com");
        Store store = new Store(
                seller,
                "가게명1",
                "02-1234-5678",
                "서울특별시 종로구 청와대로 1",
                BigDecimal.valueOf(126.978414),
                BigDecimal.valueOf(37.566680)
        );

        Store savedStore = storeRepository.save(store);
        Optional<Store> optionalStore = storeRepository.findById(savedStore.getId());

        assertThat(optionalStore).isPresent();
    }
}