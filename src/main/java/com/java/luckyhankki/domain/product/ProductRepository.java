package com.java.luckyhankki.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    //비활성화된 상품도 함께 조회
    List<Product> findAllByStoreId(Long storeId);

    //활성화 상태의 상품만 모두 조회
    List<Product> findAllByStoreIdAndIsActiveTrue(Long storeId);
}
