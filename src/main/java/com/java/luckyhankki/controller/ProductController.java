package com.java.luckyhankki.controller;

import com.java.luckyhankki.config.security.CustomUserDetails;
import com.java.luckyhankki.dto.product.*;
import com.java.luckyhankki.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Product", description = "상품 관련 API")
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @Operation(summary = "상품 등록", description = "판매자가 상품을 등록합니다.")
    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ProductRequest request) {

        ProductResponse product = service.addProduct(userDetails.getUserId(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @Operation(summary = "상품 조회", description = "상품 ID에 해당하는 상품을 조회합니다.")
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponse> getProduct(@Parameter(description = "상품 ID") @PathVariable Long productId) {
        ProductDetailResponse product = service.getProduct(productId);

        return ResponseEntity.status(HttpStatus.OK).body(product);
    }

    @Operation(summary = "상품 목록 조회", description = "활성화 상태의 등록된 모든 상품을 조회합니다.")
    @GetMapping
    public Slice<ProductResponse> getAllProducts(@ParameterObject Pageable pageable) {
        return service.getAllProducts(pageable);
    }

    @Operation(summary = "조회 조건에 따른 상품 목록 조회", description = "조회 조건에 따라 활성화 상태의 등록된 상품을 조회합니다.")
    @GetMapping("/condition")
    public Slice<ProductWithDistanceResponse> searchProductsByCondition(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "상품 조회 조건") ProductSearchCondition condition,
            @ParameterObject Pageable pageable) {

        return service.searchProductsByCondition(userDetails.getUserId(), condition, pageable);
    }

    @Operation(summary = "상품 수정", description = "상품 ID에 해당하는 상품을 수정합니다.")
    @PutMapping("/{productId}")
    public ResponseEntity<Void> updateProduct(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @Parameter(description = "상품 ID") @PathVariable Long productId,
                                              @Valid @RequestBody ProductUpdateRequest request) {

        service.updateProduct(userDetails.getUserId(), productId, request);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "상품 삭제", description = "상품 ID에 해당하는 상품을 삭제합니다.")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @Parameter(description = "상품 ID") @PathVariable Long productId) {

        service.deleteProduct(userDetails.getUserId(), productId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
