package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.seller.Seller;
import com.java.luckyhankki.domain.seller.SellerRepository;
import com.java.luckyhankki.dto.SellerRequestDTO;
import com.java.luckyhankki.dto.SellerResponseDTO;
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
    public SellerResponseDTO join(SellerRequestDTO requestDTO) {
        if (repository.existsByBusinessNumber(requestDTO.getBusinessNumber())) {
            throw new RuntimeException("이미 존재하는 사업자등록번호입니다.");
        }

        Seller seller = Seller.create(
                requestDTO.getBusinessNumber(),
                requestDTO.getName(),
                requestDTO.getPassword(),
                requestDTO.getEmail()
        );

        Seller savedSeller = repository.save(seller);

        SellerResponseDTO sellerResponseDTO = new SellerResponseDTO();
        sellerResponseDTO.setBusinessNumber(seller.getBusinessNumber());
        sellerResponseDTO.setName(savedSeller.getName());
        sellerResponseDTO.setEmail(savedSeller.getEmail());

        return sellerResponseDTO;
    }

}
