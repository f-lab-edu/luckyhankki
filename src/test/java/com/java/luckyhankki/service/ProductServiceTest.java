package com.java.luckyhankki.service;

import com.java.luckyhankki.config.security.CustomUserDetails;
import com.java.luckyhankki.domain.category.Category;
import com.java.luckyhankki.domain.category.CategoryRepository;
import com.java.luckyhankki.domain.product.Product;
import com.java.luckyhankki.domain.product.ProductRepository;
import com.java.luckyhankki.domain.reservation.ReservationRepository;
import com.java.luckyhankki.domain.reservation.ReservationStatus;
import com.java.luckyhankki.domain.store.Store;
import com.java.luckyhankki.domain.store.StoreRepository;
import com.java.luckyhankki.domain.user.UserLocationProjection;
import com.java.luckyhankki.domain.user.UserRepository;
import com.java.luckyhankki.dto.product.*;
import com.java.luckyhankki.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.math.BigDecimal;
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

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Test
    @DisplayName("승인된 가게에서 상품 추가 성공")
    void addProduct_success() {
        //given
        long sellerId = 1L;
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
        when(storeRepository.findBySellerIdAndIsApprovedTrue(sellerId))
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

        verify(storeRepository).findBySellerIdAndIsApprovedTrue(sellerId);
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

        CustomException exception = assertThrows(CustomException.class, () -> service.addProduct(storeId, request));
        assertEquals("할인된 가격은 원가보다 클 수 없습니다.", exception.getMessage());
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

        CustomException exception = assertThrows(CustomException.class, () -> service.addProduct(storeId, request));
        assertEquals("픽업 시작 시각은 픽업 종료 시간보다 늦을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("미승인된 상태에서 상품 등록할 경우 예외 발생")
    void addProduct_throwsException_whenStoreIsNotApproved() {
        // given
        long sellerId = 1L;
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

        when(storeRepository.findBySellerIdAndIsApprovedTrue(sellerId))
                .thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> service.addProduct(storeId, request));
        assertEquals("아직 승인되지 않은 가게입니다. 관리자 승인 후 상품 등록이 가능합니다.", exception.getMessage());

        verify(storeRepository).findBySellerIdAndIsApprovedTrue(storeId);
    }

    @Test
    @DisplayName("가게에 등록된 모든 상품 목록 조회")
    void getAllProducts_ByStore_whenActiveIsNull() {
        Long sellerId = 1L;
        Long storeId = 1L;
        List<ProductResponse> responseList = getProductResponses(false);

        when(storeRepository.findIdBySellerId(sellerId)).thenReturn(storeId);
        when(productRepository.findAllByStoreId(storeId))
                .thenReturn(responseList);

        List<ProductResponse> result = service.getAllProductsByStore(sellerId, null);

        assertEquals(2, result.size());

        verify(productRepository).findAllByStoreId(storeId);
    }

    @Test
    @DisplayName("가게에 등록된 활성화된 상품만 목록 조회")
    void getAllProducts_ByStore_whenActiveIsTrue() {
        Long sellerId = 1L;
        Long storeId = 1L;
        List<ProductResponse> responseList = getProductResponses(true);

        when(storeRepository.findIdBySellerId(sellerId)).thenReturn(storeId);
        when(productRepository.findAllByStoreIdAndIsActiveTrue(storeId))
                .thenReturn(responseList);

        List<ProductResponse> result = service.getAllProductsByStore(sellerId, true);

        assertEquals(1, result.size());

        verify(productRepository).findAllByStoreIdAndIsActiveTrue(storeId);
    }

    @Test
    @DisplayName("조회 조건에 따른 활성화된 상품 목록 조회")
    void searchProductsByCondition_success() {
        ProductSearchCondition condition = new ProductSearchCondition(
                null,
                ProductSearchCondition.PickupDateFilter.TODAY,
                null,
                ProductSearchCondition.SortType.PRICE);

        CustomUserDetails userDetails = Mockito.mock(CustomUserDetails.class);
        when(userDetails.getUserId()).thenReturn(1L);

        Long userId = userDetails.getUserId();
        double userLon = 126.976112;
        double userLat = 37.586671;

        when(userRepository.findUserLocationProjectionById(userId))
                .thenReturn(new UserLocationProjection() {
                    @Override
                    public BigDecimal getLongitude() {
                        return BigDecimal.valueOf(userLon);
                    }

                    @Override
                    public BigDecimal getLatitude() {
                        return BigDecimal.valueOf(userLat);
                    }
                });

        Pageable pageable = PageRequest.of(0, 10);

        List<ProductWithDistanceResponse> responseList = getProductWithDistanceResponses();
        Slice<ProductWithDistanceResponse> slice = new SliceImpl<>(responseList, pageable, false);

        when(productRepository.findAllByCondition(any(ProductSearchRequest.class), eq(pageable)))
                .thenReturn(slice);

        Slice<ProductWithDistanceResponse> result = service.searchProductsByCondition(userId, condition, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(productRepository).findAllByCondition(any(ProductSearchRequest.class), eq(pageable));
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
        long sellerId = 1L;
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

        when(productRepository.findByIdAndStore_Seller_Id(productId, sellerId))
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

        service.updateProduct(sellerId, productId, request);

        assertThat(product.getName()).isEqualTo("우동");
        assertThat(product.getStock()).isEqualTo(2);
        assertThat(product.getPriceOriginal()).isEqualTo(10000);
    }

    @Test
    @DisplayName("상품 삭제 성공")
    void deleteProduct_success() {
        Long sellerId = 1L;
        Long productId = 1L;
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

        when(productRepository.findByIdAndStore_Seller_Id(productId, sellerId))
                .thenReturn(Optional.of(product));
        when(reservationRepository.existsByProductIdAndStatusIn(productId, List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED)))
                .thenReturn(false);

        service.deleteProduct(sellerId, productId);

        verify(productRepository).delete(product);
    }

    @Test
    @DisplayName("상품 삭제 실패")
    void deleteProduct_throwsException_whenStatusIsInPending() {
        Long sellerId = 1L;
        Long productId = 1L;
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

        when(productRepository.findByIdAndStore_Seller_Id(productId, sellerId))
                .thenReturn(Optional.of(product));
        when(reservationRepository.existsByProductIdAndStatusIn(productId, List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED)))
                .thenReturn(true);

        CustomException exception = assertThrows(CustomException.class, () -> service.deleteProduct(sellerId, productId));

        assertThat(exception.getMessage()).isEqualTo("이미 예약된 상품은 삭제할 수 없습니다.");
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

    public static List<ProductWithDistanceResponse> getProductWithDistanceResponses() {
        List<ProductResponse> productResponses = getProductResponses(true);

        return productResponses.stream()
                .map(productResponse -> new ProductWithDistanceResponse(
                        productResponse,
                        BigDecimal.valueOf(2.35))
                )
                .toList();
    }
}