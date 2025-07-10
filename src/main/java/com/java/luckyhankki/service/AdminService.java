package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.report.ReportRepository;
import com.java.luckyhankki.domain.seller.Seller;
import com.java.luckyhankki.domain.seller.SellerRepository;
import com.java.luckyhankki.domain.store.Store;
import com.java.luckyhankki.domain.store.StoreRepository;
import com.java.luckyhankki.dto.admin.AdminSellerResponse;
import com.java.luckyhankki.dto.admin.AdminStoreResponse;
import com.java.luckyhankki.dto.admin.AdminStoreWithSellerResponse;
import com.java.luckyhankki.exception.CustomException;
import com.java.luckyhankki.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class AdminService {

    private final StoreRepository storeRepository;
    private final SellerRepository sellerRepository;
    private final ReportRepository reportRepository;

    public AdminService(StoreRepository storeRepository, SellerRepository sellerRepository, ReportRepository reportRepository) {
        this.storeRepository = storeRepository;
        this.sellerRepository = sellerRepository;
        this.reportRepository = reportRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminStoreResponse> findAllStore() {
        return storeRepository.findAll()
                .stream()
                .map(store -> new AdminStoreResponse(
                        store.getId(),
                        store.getName(),
                        store.getPhone(),
                        store.getAddress(),
                        store.isApproved(),
                        store.getReportCount(),
                        store.getCreatedAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminStoreWithSellerResponse findStoreByStoreId(long storeId) {
        Store store = storeRepository.findStoreAndSellerById(storeId);
        AdminStoreResponse storeResponse = new AdminStoreResponse(
                store.getId(),
                store.getName(),
                store.getPhone(),
                store.getAddress(),
                store.isApproved(),
                store.getReportCount(),
                store.getCreatedAt()
        );

        Seller seller = store.getSeller();
        AdminSellerResponse sellerResponse = new AdminSellerResponse(
                seller.getId(),
                seller.getBusinessNumber(),
                seller.getName(),
                seller.getEmail()
        );

        return new AdminStoreWithSellerResponse(storeResponse, sellerResponse);
    }

    @Transactional
    public void approveStore(long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));
        store.approveStore();
    }
}
