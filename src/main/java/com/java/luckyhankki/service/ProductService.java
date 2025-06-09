package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.category.Category;
import com.java.luckyhankki.domain.category.CategoryRepository;
import com.java.luckyhankki.domain.product.Product;
import com.java.luckyhankki.domain.product.ProductRepository;
import com.java.luckyhankki.domain.store.Store;
import com.java.luckyhankki.domain.store.StoreRepository;
import com.java.luckyhankki.dto.ProductDetailResponse;
import com.java.luckyhankki.dto.ProductRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, StoreRepository storeRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
        this.categoryRepository = categoryRepository;
    }

    public Product addProduct(Long storeId, ProductRequest request) {
        if (request.priceDiscount() > request.priceOriginal()) {
            throw new RuntimeException("할인된 가격은 원가보다 클 수 없습니다.");
        }

        LocalDateTime pickupStartDateTime = request.pickupStartDateTime();
        LocalDateTime pickupEndDateTime = request.pickupEndDateTime();

        if (pickupStartDateTime.isAfter(pickupEndDateTime)) {
            throw new RuntimeException("픽업 시작 시간은 픽업 종료 시간보다 늦을 수 없습니다.");
        }

        //가게 승인이 된 상태여야 상품 등록 가능
        Store store = storeRepository.findByIdAndIsApprovedTrue(storeId)
                .orElseThrow(() -> new RuntimeException("아직 승인되지 않은 가게입니다. 관리자 승인 후 상품 등록이 가능합니다."));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 카테고리입니다."));

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

        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts(Long storeId, Boolean active) {
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
                .orElseThrow(() -> new RuntimeException("해당 상품이 존재하지 않습니다."));

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
}
