package com.java.luckyhankki.domain.product;

import com.java.luckyhankki.domain.category.Category;
import com.java.luckyhankki.domain.category.CategoryRepository;
import com.java.luckyhankki.domain.seller.Seller;
import com.java.luckyhankki.domain.seller.SellerRepository;
import com.java.luckyhankki.domain.store.Store;
import com.java.luckyhankki.domain.store.StoreRepository;
import com.java.luckyhankki.dto.product.ProductResponse;
import com.java.luckyhankki.dto.product.ProductSearchCondition;
import com.java.luckyhankki.dto.product.ProductSearchRequest;
import com.java.luckyhankki.dto.product.ProductWithDistanceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    private CategoryRepository categoryRepository;

    private Store savedStore;

    private Long categoryBakeryId;

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

        Category categoryFood = new Category("음식");
        categoryRepository.save(categoryFood);

        Category categoryBakery = new Category("빵");
        categoryBakeryId = categoryRepository.save(categoryBakery).getId();

        // 상품 저장 (isActive = true)
        Product productBibimbap = new Product(
                store,
                categoryFood,
                "비빔밥",
                10000,
                8000,
                3,
                "육회비빔밥 입니다.",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );
        productRepository.save(productBibimbap);

        Product productBread = new Product(
                store,
                categoryBakery,
                "소금빵",
                5000,
                2000,
                1,
                "소금빵 입니다.",
                LocalDateTime.now().plusMinutes(2),
                LocalDateTime.now().plusHours(6)
        );
        productRepository.save(productBread);

        // 상품 저장 (isActive = false)
        Product inactiveProduct = new Product(
                store,
                categoryFood,
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
        List<ProductResponse> products = productRepository.findAllByStoreId(savedStore.getId());

        assertThat(products).hasSize(3);
    }

    @Test
    @DisplayName("storeId에 소속된 isActive가 true인 상품만 조회")
    void testFindAllByStoreIdAndIsActiveTrue() {
        List<ProductResponse> products = productRepository.findAllByStoreIdAndIsActiveTrue(savedStore.getId());

        assertThat(products).hasSize(2);
    }

    @Test
    @DisplayName("isActive가 true인 상품만 조회")
    void testFindAllByIsActiveTrue() {
        Pageable pageable = PageRequest.of(0, 10);
        Slice<ProductResponse> products = productRepository.findAllByIsActiveTrue(pageable);

        assertThat(products).hasSize(2);
    }

    @Test
    @DisplayName("카테고리가 베이커리이고, 픽업 날짜가 오늘인 상품 조회")
    void testFindAllByCondition() {
        ProductSearchCondition condition = new ProductSearchCondition(
                categoryBakeryId,
                ProductSearchCondition.PickupDateFilter.TODAY,
                null,
                ProductSearchCondition.SortType.DISTANCE);

        double userLon = 126.976112;
        double userLat = 37.586671;

        ProductSearchRequest request = new ProductSearchRequest(condition, userLat, userLon);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Slice<ProductWithDistanceResponse> result = productRepository.findAllByCondition(request, pageRequest);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).distance()).isBetween(BigDecimal.valueOf(2.23), BigDecimal.valueOf(2.25));
        assertThat(result.getContent().get(0).product().name()).isEqualTo("소금빵");
    }

    @Test
    @DisplayName("판매자 ID와 상품 ID로 상품 조회")
    void testFindByIdAndStore_Seller_Id() {
        Long productId = productRepository.findAll().get(0).getId();
        Long sellerId = sellerRepository.findAll().get(0).getId();

        Optional<Product> product = productRepository.findByIdAndStore_Seller_Id(productId, sellerId);

        assertThat(product.isPresent()).isTrue();
    }
}