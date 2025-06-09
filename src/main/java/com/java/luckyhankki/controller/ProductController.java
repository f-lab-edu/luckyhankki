package com.java.luckyhankki.controller;

import com.java.luckyhankki.domain.product.Product;
import com.java.luckyhankki.dto.ProductRequest;
import com.java.luckyhankki.service.ProductService;
import jakarta.validation.Valid;
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
    public ResponseEntity<Product> addProduct(
            @RequestParam Long storeId, @Valid @RequestBody ProductRequest request) {

        Product product = service.addProduct(storeId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable Long productId) {
        Product product = service.getProduct(productId);

        return ResponseEntity.status(HttpStatus.OK).body(product);
    }
}
