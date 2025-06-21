package com.java.luckyhankki.controller;

import com.java.luckyhankki.dto.product.ProductResponse;
import com.java.luckyhankki.dto.StoreRequest;
import com.java.luckyhankki.dto.StoreResponse;
import com.java.luckyhankki.service.ProductService;
import com.java.luckyhankki.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Store", description = "가게 관련 API")
@RestController
@RequestMapping("/stores")
public class StoreController {

    private final StoreService storeService;
    private final ProductService productService;

    public StoreController(StoreService storeService, ProductService productService) {
        this.storeService = storeService;
        this.productService = productService;
    }

    @Operation(summary = "가게 등록", description = "판매자가 가게를 등록합니다.")
    @PostMapping
    public ResponseEntity<StoreResponse> registerStore(
            @Parameter(description = "판매자 ID") @RequestParam Long sellerId,
            @Valid @RequestBody StoreRequest store
    ) {
        StoreResponse storeResponse = storeService.registerStore(sellerId, store);

        return ResponseEntity.status(HttpStatus.CREATED).body(storeResponse);
    }

    @Operation(summary = "가게 조회", description = "판매자 ID에 해당하는 가게를 조회합니다.")
    @GetMapping("/{sellerId}")
    public ResponseEntity<StoreResponse> getStore(@Parameter(description = "판매자 ID") @PathVariable Long sellerId) {
        StoreResponse storeResponse = storeService.findStore(sellerId);

        return ResponseEntity.status(HttpStatus.OK).body(storeResponse);
    }

    @Operation(summary = "가게에 등록된 상품 조회", description = "판매자가 자신의 가게에 등록된 모든 상품을 조회합니다.")
    @GetMapping("/{storeId}/products")
    public ResponseEntity<List<ProductResponse>> getProducts(
            @Parameter(description = "가게 ID") @PathVariable Long storeId,
            @Parameter(description = "상품 활성화 여부") @RequestParam(required = false) Boolean active) {

        List<ProductResponse> products = productService.getAllProductsByStore(storeId, active);

        return ResponseEntity.status(HttpStatus.OK).body(products);
    }
}
