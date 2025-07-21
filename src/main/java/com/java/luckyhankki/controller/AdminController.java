package com.java.luckyhankki.controller;

import com.java.luckyhankki.dto.admin.AdminStoreResponse;
import com.java.luckyhankki.dto.admin.AdminStoreWithSellerResponse;
import com.java.luckyhankki.dto.admin.AdminReportHandleRequest;
import com.java.luckyhankki.dto.admin.AdminReportHandleResponse;
import com.java.luckyhankki.dto.admin.AdminReportListResponse;
import com.java.luckyhankki.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * TODO
 * 1. 가게 반려 처리
 */
@Tag(name = "Admin", description = "관리자 관련 API")
@RestController
@RequestMapping("/admins")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(summary = "등록된 모든 가게 조회", description = "관리자가 등록된 가게 목록을 조회합니다.")
    @GetMapping("/stores")
    public ResponseEntity<List<AdminStoreResponse>> findAllStore() {
        List<AdminStoreResponse> stores = adminService.findAllStore();

        return ResponseEntity.status(HttpStatus.OK).body(stores);

    }

    @Operation(summary = "가게 및 판매자 조회", description = "관리자가 가게 ID에 해당하는 가게 및 판매자 정보를 조회합니다.")
    @GetMapping("/stores/{storeId}")
    public ResponseEntity<AdminStoreWithSellerResponse> findStoreByStoreId(@Parameter(description = "가게 ID") @PathVariable long storeId) {
        AdminStoreWithSellerResponse result = adminService.findStoreByStoreId(storeId);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Operation(summary = "가게 승인 처리", description = "관리자가 등록된 가게를 검토 후 승인 처리를 합니다.")
    @PutMapping("/stores/{storeId}")
    public ResponseEntity<Void> approveStore(@PathVariable("storeId") long storeId) {
        adminService.approveStore(storeId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "신고 목록 조회", description = "관리자가 등록된 신고 목록을 조회합니다.")
    @GetMapping("/reports")
    public Slice<AdminReportListResponse> findAllReports(@PageableDefault(sort = "id") Pageable pageable) {
        return adminService.getReports(pageable);
    }

    @Operation(summary = "신고 처리", description = "관리자가 등록된 신고를 처리합니다.")
    @PutMapping("/reports/{reportId}")
    public ResponseEntity<AdminReportHandleResponse> handleReport(
            @Parameter(description = "신고 ID") @PathVariable("reportId") Long reportId,
            @Valid @RequestBody AdminReportHandleRequest request) {

        AdminReportHandleResponse result = adminService.handleReport(reportId, request);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
