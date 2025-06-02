package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.seller.Seller;
import com.java.luckyhankki.domain.seller.SellerProjection;
import com.java.luckyhankki.domain.seller.SellerRepository;
import com.java.luckyhankki.dto.SellerRequest;
import com.java.luckyhankki.dto.SellerResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SellerService {

    private final SellerRepository repository;

    public SellerService(SellerRepository repository) {
        this.repository = repository;
    }

    /**
     * 판매자 회원가입
     * TODO 비밀번호 암호화, Exception 만들기
     */
    @Transactional
    public SellerResponse join(SellerRequest request) {
        if (repository.existsByBusinessNumber(request.businessNumber())) {
            throw new RuntimeException("이미 존재하는 사업자등록번호입니다.");
        }

        Seller seller = Seller.create(
                request.businessNumber(),
                request.name(),
                request.password(),
                request.email()
        );

        Seller savedSeller = repository.save(seller);

        return new SellerResponse(
                savedSeller.getBusinessNumber(),
                savedSeller.getName(),
                savedSeller.getEmail()
        );
    }

    /**
     * 판매자 단건 조회
     * TODO Custom Exception 만들기
     */
    @Transactional(readOnly = true)
    public SellerResponse findSellerByBusinessNumber(String businessNumber) {
        SellerProjection seller = repository.findByBusinessNumber(businessNumber)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 판매자입니다."));

        return new SellerResponse(businessNumber, seller.getName(), seller.getEmail());
    }
}
