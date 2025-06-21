package com.java.luckyhankki.domain.product;

import com.java.luckyhankki.dto.product.ProductResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
    //해당 가게의 비활성화된 상품도 함께 조회
    List<ProductResponse> findAllByStoreId(Long storeId);

    //해당 가게의 활성화 상태의 상품만 모두 조회
    List<ProductResponse> findAllByStoreIdAndIsActiveTrue(Long storeId);

    //활성화된 상태의 상품 모두 조회
    Slice<ProductResponse> findAllByIsActiveTrue(Pageable pageable);
}
