package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.seller.Seller;
import com.java.luckyhankki.domain.seller.SellerRepository;
import com.java.luckyhankki.dto.SellerRequest;
import com.java.luckyhankki.dto.SellerResponse;
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
     * TODO Exception 만들기
     */
    @Transactional
    public SellerResponse join(SellerRequest request) {
        if (repository.existsByBusinessNumber(request.businessNumber())) {
            throw new RuntimeException("이미 존재하는 사업자등록번호입니다.");
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
