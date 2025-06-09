package com.java.luckyhankki.domain.product;

import com.java.luckyhankki.domain.seller.Seller;
import com.java.luckyhankki.domain.seller.SellerRepository;
import com.java.luckyhankki.domain.store.Store;
import com.java.luckyhankki.domain.store.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProductTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private SellerRepository sellerRepository;

    private Store savedStore;

    @BeforeEach
    void setUp() {
        Seller seller = new Seller("1234567890", "판매자1", "abc!@#$5", "abc@test.com");
        Seller savedSeller = sellerRepository.save(seller);

        Store store = new Store(
                savedSeller,
                "원조비빔밥가게",
                "031-123-4567",
                "경기도 수원시",
                BigDecimal.valueOf(126.978414),
                BigDecimal.valueOf(37.566680)
        );
        savedStore = storeRepository.save(store);

        // 상품 저장 (isActive = true)
        Product activeProduct = new Product(
                store,
                null,
                "비빔밥",
                10000,
                8000,
                3,
                "육회비빔밥입니다.",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );
        productRepository.save(activeProduct);

        // 상품 저장 (isActive = false)
        Product inactiveProduct = new Product(
                store,
                null,
                "김밥",
                5000,
                4500,
                5,
                "참치김밥입니다.",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );
        inactiveProduct.deactivate(); //상품 비활성화
        productRepository.save(inactiveProduct);
    }

    @Test
    @DisplayName("storeId로 전체 상품 조회")
    void testFindAllByStoreId() {
        List<Product> products = productRepository.findAllByStoreId(savedStore.getId());

        assertThat(products).hasSize(2);
    }

    @Test
    @DisplayName("isActive가 true인 상품만 조회")
    void testFindAllByStoreIdAndIsActiveTrue() {
        List<Product> products = productRepository.findAllByStoreIdAndIsActiveTrue(savedStore.getId());

        assertThat(products).hasSize(1);
        assertThat(products.get(0).isActive()).isTrue();
    }
}