package com.java.luckyhankki.service;

import com.java.luckyhankki.config.security.CustomUserDetails;
import com.java.luckyhankki.domain.category.Category;
import com.java.luckyhankki.domain.category.CategoryRepository;
import com.java.luckyhankki.domain.product.Product;
import com.java.luckyhankki.domain.product.ProductRepository;
import com.java.luckyhankki.domain.store.Store;
import com.java.luckyhankki.domain.store.StoreRepository;
import com.java.luckyhankki.domain.user.UserLocationProjection;
import com.java.luckyhankki.domain.user.UserRepository;
import com.java.luckyhankki.dto.product.*;
import com.java.luckyhankki.exception.CustomException;
import com.java.luckyhankki.exception.ErrorCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public ProductService(ProductRepository productRepository, StoreRepository storeRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ProductResponse addProduct(Long storeId, ProductRequest request) {
        if (request.priceDiscount() > request.priceOriginal()) {
            throw new CustomException(ErrorCode.INVALID_PRICE_DISCOUNT);
        }

        LocalDateTime pickupStartDateTime = request.pickupStartDateTime();
        LocalDateTime pickupEndDateTime = request.pickupEndDateTime();

        if (pickupStartDateTime.isAfter(pickupEndDateTime)) {
            throw new CustomException(ErrorCode.INVALID_PICKUP_TIME_RANGE);
        }

        //가게 승인이 된 상태여야 상품 등록 가능
        Store store = storeRepository.findByIdAndIsApprovedTrue(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_APPROVED));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Product product = new Product(
                store,
                category,
                request.name(),
                request.priceOriginal(),
                request.priceDiscount(),
                request.stock(),
                request.description(),
                pickupStartDateTime,
                pickupEndDateTime
        );

        Product savedProduct = productRepository.save(product);

        return new ProductResponse(
                savedProduct.getId(),
                savedProduct.getStore().getName(),
                savedProduct.getCategory().getName(),
                savedProduct.getName(),
                savedProduct.getPriceOriginal(),
                savedProduct.getPriceDiscount(),
                savedProduct.getStock(),
                savedProduct.getPickupStartDateTime(),
                savedProduct.getPickupEndDateTime()
        );
    }

    /**
     * 판매자가 자신의 가게에 등록된 상품 목록을 조회
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProductsByStore(Long storeId, Boolean active) {
        //active가 null이거나 false이면 active 활성화 여부와 상관 없이 모두 조회
        if (active == null || !active) {
            return productRepository.findAllByStoreId(storeId);
        }

        //그 외에는 활성화된 상풍만 모두 조회
        return productRepository.findAllByStoreIdAndIsActiveTrue(storeId);
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        return new ProductDetailResponse(
                product.getStore().getName(),
                product.getStore().getAddress(),
                product.getStore().getPhone(),
                product.getCategory().getName(),
                product.getName(),
                product.getDescription(),
                product.getStock(),
                product.getPriceOriginal(),
                product.getPriceDiscount(),
                product.getPickupStartDateTime(),
                product.getPickupEndDateTime()
        );
    }

    /**
     * 고객이 등록된 모든 상품 목록을 조회
     */
    @Transactional(readOnly = true)
    public Slice<ProductResponse> getAllProducts(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.ASC, "priceDiscount")
            );
        }

        Slice<ProductResponse> products = productRepository.findAllByIsActiveTrue(pageable);

        return products.map(product -> new ProductResponse(
                product.id(),
                product.storeName(),
                product.categoryName(),
                product.name(),
                product.priceOriginal(),
                product.priceDiscount(),
                product.stock(),
                product.pickupStartDateTime(),
                product.pickupEndDateTime()
        ));
    }

    /**
     * 고객이 조회 조건에 따른 상품 목록을 동적 조회
     */
    @Transactional(readOnly = true)
    public Slice<ProductWithDistanceResponse> searchProductsByCondition(CustomUserDetails userDetails,
                                                                        ProductSearchCondition searchCondition,
                                                                        Pageable pageable) {
        //TODO userId로 추후 변경
        UserLocationProjection location = userRepository.findUserLocationProjectionByEmail(userDetails.getUsername());
        double userLat = location.getLatitude().doubleValue();
        double userLon = location.getLongitude().doubleValue();

        ProductSearchRequest request = new ProductSearchRequest(searchCondition, userLat, userLon);
        Slice<ProductWithDistanceResponse> products = productRepository.findAllByCondition(request, pageable);

        return products.map(distanceResponse -> new ProductWithDistanceResponse(
                new ProductResponse(
                        distanceResponse.product().id(),
                        distanceResponse.product().storeName(),
                        distanceResponse.product().categoryName(),
                        distanceResponse.product().name(),
                        distanceResponse.product().priceOriginal(),
                        distanceResponse.product().priceDiscount(),
                        distanceResponse.product().stock(),
                        distanceResponse.product().pickupStartDateTime(),
                        distanceResponse.product().pickupEndDateTime()
                ),
                distanceResponse.distance()
        ));
    }

    @Transactional
    public void updateProduct(Long productId, ProductUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
            product.changeCategory(category);
        }

        product.updateProduct(request);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        //if 예약건이 없는 경우
        productRepository.deleteById(productId);
        //else 예외 던지기
    }
}
