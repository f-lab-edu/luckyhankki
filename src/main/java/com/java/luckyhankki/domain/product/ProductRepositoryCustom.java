package com.java.luckyhankki.domain.product;

import com.java.luckyhankki.dto.product.ProductResponse;
import com.java.luckyhankki.dto.product.ProductSearchCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ProductRepositoryCustom {
    Slice<ProductResponse> findAllByCondition(ProductSearchCondition condition, Pageable pageable);
}
