package com.java.luckyhankki.controller;

import com.java.luckyhankki.dto.SellerRequest;
import com.java.luckyhankki.dto.SellerResponse;
import com.java.luckyhankki.service.SellerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Seller", description = "판매자 관련 API")
@RestController
@RequestMapping("/sellers")
public class SellerController {

    private final SellerService service;

    public SellerController(SellerService service) {
        this.service = service;
    }

    @Operation(summary = "판매자 회원가입", description = "판매자가 회원가입을 진행합니다.")
    @PostMapping
    public ResponseEntity<SellerResponse> createSeller(@Valid @RequestBody SellerRequest seller) {
        SellerResponse sellerResponse = service.join(seller);

        return ResponseEntity.status(HttpStatus.CREATED).body(sellerResponse);
    }

}
