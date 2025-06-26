package com.java.luckyhankki.controller;

import com.java.luckyhankki.dto.LoginResult;
import com.java.luckyhankki.dto.SellerRequest;
import com.java.luckyhankki.dto.SellerResponse;
import com.java.luckyhankki.dto.seller.SellerLoginRequest;
import com.java.luckyhankki.service.AuthService;
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

    private final SellerService sellerService;
    private final AuthService authService;

    public SellerController(SellerService sellerService, AuthService authService) {
        this.sellerService = sellerService;
        this.authService = authService;
    }

    @Operation(summary = "판매자 회원가입", description = "판매자가 회원가입을 진행합니다.")
    @PostMapping
    public ResponseEntity<SellerResponse> createSeller(@Valid @RequestBody SellerRequest seller) {
        SellerResponse sellerResponse = sellerService.join(seller);

        return ResponseEntity.status(HttpStatus.CREATED).body(sellerResponse);
    }

    @Operation(summary = "로그인", description = "로그인을 진행합니다.")
    @PostMapping("/login")
    public ResponseEntity<LoginResult> login(@Valid @RequestBody SellerLoginRequest request) {
        LoginResult result = authService.sellerLogin(request);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
