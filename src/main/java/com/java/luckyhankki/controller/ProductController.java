package com.java.luckyhankki.controller;

import com.java.luckyhankki.dto.ProductDetailResponse;
import com.java.luckyhankki.dto.ProductRequest;
import com.java.luckyhankki.dto.ProductResponse;
import com.java.luckyhankki.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(
            @RequestParam Long storeId, @Valid @RequestBody ProductRequest request) {

        ProductResponse product = service.addProduct(storeId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponse> getProduct(@PathVariable Long productId) {
        ProductDetailResponse product = service.getProduct(productId);

        return ResponseEntity.status(HttpStatus.OK).body(product);
    }

    @GetMapping
    public Slice<ProductResponse> getAllProducts(Pageable pageable) {
        return service.getAllProducts(pageable);
    }
}
