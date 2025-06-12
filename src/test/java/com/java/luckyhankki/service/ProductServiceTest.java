package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.category.Category;
import com.java.luckyhankki.domain.category.CategoryRepository;
import com.java.luckyhankki.domain.product.Product;
import com.java.luckyhankki.domain.product.ProductRepository;
import com.java.luckyhankki.domain.store.Store;
import com.java.luckyhankki.domain.store.StoreRepository;
import com.java.luckyhankki.dto.ProductDetailResponse;
import com.java.luckyhankki.dto.ProductRequest;
import com.java.luckyhankki.dto.ProductResponse;
import com.java.luckyhankki.dto.ProductUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("승인된 가게에서 상품 추가 성공")
    void addProduct_success() {
        //given
        long storeId = 1L;
        long categoryId = 1L;
        ProductRequest request = new ProductRequest(
                categoryId,
                "비빔밥",
                10000,
                8000,
                1,
                "육회비빔밥 입니다.",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2));

        //Store mock
        Store store = mock(Store.class);
        when(storeRepository.findByIdAndIsApprovedTrue(storeId))
                .thenReturn(Optional.of(store));

        //Category mock
        Category category = mock(Category.class);
        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(productRepository.save(any(Product.class)))
                .then(returnsFirstArg());

        ProductResponse product = service.addProduct(storeId, request);

        assertEquals("비빔밥", product.name());
        assertEquals(10000, product.priceOriginal());
        assertEquals(request.pickupStartDateTime(), product.pickupStartDateTime());

        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("할인된 가격이 원가보다 클 경우 예외 발생")
    void addProduct_throwsException_whenDiscountPriceIsBiggerThanOriginalPrice() {
        // given
        long storeId = 1L;
        long categoryId = 1L;

        ProductRequest request = new ProductRequest(
                categoryId,
                "비빔밥",
                10000,
                500000, // 할인 가격이 원가보다 큼
                1,
                "잘못된 가격",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        assertThrows(RuntimeException.class, () -> service.addProduct(storeId, request));
    }

    @Test
    @DisplayName("픽업 시작 시간이 픽업 종료 시간보다 늦을 경우 예외 발생")
    void addProduct_throwsException_whenPickupStartDateIsLaterThanPickupEndDate() {
        // given
        long storeId = 1L;
        long categoryId = 1L;

        ProductRequest request = new ProductRequest(
                categoryId,
                "비빔밥",
                10000,
                8000,
                1,
                "픽업 시작 시간이 픽업 종료 시간보다 늦습니다.",
                LocalDateTime.now().plusHours(6),
                LocalDateTime.now().plusHours(2)
        );

        assertThrows(RuntimeException.class, () -> service.addProduct(storeId, request));
    }

    @Test
    @DisplayName("미승인된 상태에서 상품 등록할 경우 예외 발생")
    void addProduct_throwsException_whenStoreIsNotApproved() {
        // given
        long storeId = 1L;
        long categoryId = 1L;

        ProductRequest request = new ProductRequest(
                categoryId,
                "비빔밥",
                10000,
                8000,
                1,
                "미승인 가게",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        when(storeRepository.findByIdAndIsApprovedTrue(storeId))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.addProduct(storeId, request));
    }

    @Test
    @DisplayName("가게에 등록된 모든 상품 목록 조회")
    void getAllProducts_ByStore_whenActiveIsNull() {
        long storeId = 1L;
        List<ProductResponse> responseList = getProductResponses(false);

        when(productRepository.findAllByStoreId(storeId))
                .thenReturn(responseList);

        List<ProductResponse> result = service.getAllProductsByStore(storeId, null);

        assertEquals(2, result.size());

        verify(productRepository).findAllByStoreId(storeId);
    }

    @Test
    @DisplayName("가게에 등록된 활성화된 상품만 목록 조회")
    void getAllProducts_ByStore_whenActiveIsTrue() {
        long storeId = 1L;
        List<ProductResponse> responseList = getProductResponses(true);

        when(productRepository.findAllByStoreIdAndIsActiveTrue(storeId))
                .thenReturn(responseList);

        List<ProductResponse> result = service.getAllProductsByStore(storeId, true);

        assertEquals(1, result.size());

        verify(productRepository).findAllByStoreIdAndIsActiveTrue(storeId);
    }

    @Test
    @DisplayName("상품 상세 조회 성공")
    void getProduct_success() {
        long productId = 1L;

        //Store mock
        Store store = mock(Store.class);
        when(store.getName()).thenReturn("가게명1");

        //Category mock
        Category category = mock(Category.class);
        when(category.getName()).thenReturn("음식");

        Product product = new Product(
                store,
                category,
                "비빔밥",
                10000,
                8000,
                1,
                "육회비빔밥 입니다.",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        ProductDetailResponse response = service.getProduct(productId);

        assertThat(response.storeName()).isEqualTo(store.getName());
        assertThat(response.categoryName()).isEqualTo(category.getName());
        assertThat(response.productName()).isEqualTo(product.getName());

        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("상품 업데이트 테스트")
    void updateProduct_success() {
        long productId = 1L;
        Store store = mock(Store.class);
        Category category = mock(Category.class);

        Product product = new Product(
                store,
                category,
                "비빔밥",
                10000,
                8000,
                1,
                "육회비빔밥 입니다.",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        ProductUpdateRequest request = new ProductUpdateRequest(
                null,
                 "우동",
                null,
                null,
                2,
                null,
                null,
                null
        );

        service.updateProduct(productId, request);

        assertThat(product.getName()).isEqualTo("우동");
        assertThat(product.getStock()).isEqualTo(2);
        assertThat(product.getPriceOriginal()).isEqualTo(10000);
    }

    private static List<ProductResponse> getProductResponses(boolean isActive) {
        Store store = mock(Store.class);
        when(store.getName()).thenReturn("가게1");

        Category category = mock(Category.class);
        when(category.getName()).thenReturn("음식");

        Product product1 = mock(Product.class);
        Product product2 = mock(Product.class);
        lenient().when(product1.isActive()).thenReturn(true);
        lenient().when(product2.isActive()).thenReturn(false);

        List<Product> products = List.of(product1, product2);
        List<Product> filterProducts = (isActive ? products.stream().filter(Product::isActive).toList() : products);

        return filterProducts.stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        store.getName(),
                        category.getName(),
                        product.getName(),
                        product.getPriceOriginal(),
                        product.getPriceDiscount(),
                        product.getStock(),
                        product.getPickupStartDateTime(),
                        product.getPickupEndDateTime()))
                .toList();
    }
}