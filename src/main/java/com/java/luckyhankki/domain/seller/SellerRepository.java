package com.java.luckyhankki.domain.seller;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    //사업자등록번호 중복확인
    boolean existsByBusinessNumber(String businessNumber);
    
    Optional<Seller> findByBusinessNumber(String businessNumber);
}
