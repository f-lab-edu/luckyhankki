package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.seller.Seller;
import com.java.luckyhankki.domain.seller.SellerRepository;
import com.java.luckyhankki.dto.seller.SellerRequest;
import com.java.luckyhankki.dto.seller.SellerResponse;
import com.java.luckyhankki.exception.CustomException;
import com.java.luckyhankki.exception.ErrorCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SellerService {

    private final SellerRepository repository;
    private final PasswordEncoder passwordEncoder;

    public SellerService(SellerRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 판매자 회원가입
     */
    @Transactional
    public SellerResponse join(SellerRequest request) {
        if (repository.existsByBusinessNumber(request.businessNumber())) {
            throw new CustomException(ErrorCode.BUSINESS_NUMBER_ALREADY_EXISTS);
        }

        Seller seller = new Seller(request.businessNumber(), request.name(), request.password(), request.email());
        seller.changePassword(passwordEncoder.encode(request.password()));

        Seller savedSeller = repository.save(seller);

        return new SellerResponse(
                savedSeller.getBusinessNumber(),
                savedSeller.getName(),
                savedSeller.getEmail()
        );
    }
}
