package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.seller.Seller;
import com.java.luckyhankki.domain.seller.SellerRepository;
import com.java.luckyhankki.domain.store.Store;
import com.java.luckyhankki.domain.store.StoreProjection;
import com.java.luckyhankki.domain.store.StoreRepository;
import com.java.luckyhankki.dto.StoreRequest;
import com.java.luckyhankki.dto.StoreResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StoreService {

    private final SellerRepository sellerRepository;
    private final StoreRepository storeRepository;

    public StoreService(SellerRepository sellerRepository, StoreRepository storeRepository) {
        this.sellerRepository = sellerRepository;
        this.storeRepository = storeRepository;
    }

    public StoreResponse registerStore(Long sellerId, StoreRequest request) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found: " + sellerId));

        //만약 seller가 이미 가게 등록을 한 상태라면 하나 이상의 가게는 등록할 수 없다고 exception
        if (storeRepository.existsStoreBySellerId(sellerId)) {
            throw new RuntimeException("이미 등록된 가게가 있습니다.");
        }

        Store store = new Store(
                seller,
                request.name(),
                request.phone(),
                request.address(),
                request.longitude(),
                request.latitude());

        Store savedStore = storeRepository.save(store);

        return new StoreResponse(
                savedStore.getId(),
                savedStore.getName(),
                savedStore.getPhone(),
                savedStore.getAddress(),
                savedStore.isApproved(),
                savedStore.getReportCount());
    }

    @Transactional(readOnly = true)
    public StoreResponse findStore(Long sellerId) {
        StoreProjection store = storeRepository.findStoreBySellerId(sellerId)
                .orElseThrow(() -> new RuntimeException("아직 가게가 등록되지 않았습니다."));

        return new StoreResponse(
                store.getId(),
                store.getName(),
                store.getPhone(),
                store.getAddress(),
                store.getIsApproved(),
                store.getReportCount());
    }
}
