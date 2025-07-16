package com.java.luckyhankki.domain.product;

import com.java.luckyhankki.dto.product.ProductSearchRequest;
import com.java.luckyhankki.dto.product.ProductWithDistanceResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ProductRepositoryCustom {
    Slice<ProductWithDistanceResponse> findAllByCondition(ProductSearchRequest request, Pageable pageable);
}
