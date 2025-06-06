package com.java.luckyhankki.controller;

import com.java.luckyhankki.dto.SellerRequest;
import com.java.luckyhankki.dto.SellerResponse;
import com.java.luckyhankki.service.SellerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sellers")
public class SellerController {

    private final SellerService service;

    public SellerController(SellerService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SellerResponse> createSeller(@Valid @RequestBody SellerRequest seller) {
        SellerResponse sellerResponse = service.join(seller);

        return ResponseEntity.status(HttpStatus.CREATED).body(sellerResponse);
    }

}
