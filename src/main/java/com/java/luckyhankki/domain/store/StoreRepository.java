package com.java.luckyhankki.domain.store;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    boolean existsStoreBySellerId(Long sellerId);
    Optional<StoreProjection> findStoreBySellerId(Long sellerId);
}
