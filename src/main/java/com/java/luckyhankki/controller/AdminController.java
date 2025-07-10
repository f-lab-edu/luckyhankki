package com.java.luckyhankki.controller;

import com.java.luckyhankki.dto.admin.AdminStoreResponse;
import com.java.luckyhankki.dto.admin.AdminStoreWithSellerResponse;
import com.java.luckyhankki.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * TODO
 * 1. 신고 내역 조회
 * 2. 신고 내역 조치
 * 3. 가게 반려 처리
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
}
