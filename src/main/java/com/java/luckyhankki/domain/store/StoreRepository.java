package com.java.luckyhankki.domain.store;

import com.java.luckyhankki.dto.admin.AdminStoreResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    boolean existsStoreBySellerId(Long sellerId);

    Optional<StoreProjection> findStoreBySellerId(Long sellerId);

    //승인된 가게 조회
    Optional<Store> findBySellerIdAndIsApprovedTrue(Long sellerId);

    @EntityGraph(attributePaths = {"seller"})
    Store findStoreAndSellerById(Long storeId);

    Page<AdminStoreResponse> findAllByIsActiveTrue(Pageable pageable);

    @EntityGraph(attributePaths = {"seller"})
    Long findIdBySellerId(Long sellerId);
}
