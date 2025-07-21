package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.report.Report;
import com.java.luckyhankki.domain.report.ReportRepository;
import com.java.luckyhankki.domain.report.ReportStatus;
import com.java.luckyhankki.domain.seller.Seller;
import com.java.luckyhankki.domain.store.Store;
import com.java.luckyhankki.domain.store.StoreRepository;
import com.java.luckyhankki.dto.admin.*;
import com.java.luckyhankki.exception.CustomException;
import com.java.luckyhankki.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    private final StoreRepository storeRepository;
    private final ReportRepository reportRepository;

    public AdminService(StoreRepository storeRepository, ReportRepository reportRepository) {
        this.storeRepository = storeRepository;
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

    @Transactional(readOnly = true)
    public Slice<AdminReportListResponse> getReports(Pageable pageable) {
        return reportRepository.findAllByIsCompletedFalse(pageable);
    }

    @Transactional
    public AdminReportHandleResponse handleReport(Long reportId, AdminReportHandleRequest request) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND));

        report.handleReport(request.reportStatus(), request.adminMemo());

        Store store = reportRepository.findStoreById(reportId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        if (request.reportStatus() == ReportStatus.RESOLVED) {
            store.addReport();
            log.info("[handleReport] store id: {}, report count: {}", store.getId(), store.getReportCount());
        }

        return new AdminReportHandleResponse(
                report.getId(),
                report.getStatus(),
                report.getAdminMemo(),
                store.getId(),
                store.getReportCount()
        );
    }
}
