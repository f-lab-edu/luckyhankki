package com.java.luckyhankki.domain.store;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    boolean existsStoreBySellerId(Long sellerId);
    Optional<StoreProjection> findStoreBySellerId(Long sellerId);

    //승인된 가게 조회
    Optional<Store> findByIdAndIsApprovedTrue(Long storeId);

    @EntityGraph(attributePaths = {"seller"})
    Store findStoreAndSellerById(Long storeId);

}
