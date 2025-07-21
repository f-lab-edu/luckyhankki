package com.java.luckyhankki.controller;

import com.java.luckyhankki.config.security.CustomUserDetails;
import com.java.luckyhankki.dto.report.ReportListResponse;
import com.java.luckyhankki.dto.report.ReportRequest;
import com.java.luckyhankki.dto.report.ReportResponse;
import com.java.luckyhankki.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Report", description = "신고 관련 API")
@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(summary = "신고 등록", description = "사용자가 상품을 신고합니다.")
    @PostMapping
    public ResponseEntity<ReportResponse> addReport(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ReportRequest request) {

        ReportResponse report = reportService.addReport(userDetails.getUserId(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }

    @Operation(summary = "신고 목록 조회", description = "사용자가 작성한 신고 목록을 조회합니다.")
    @GetMapping
    public Slice<ReportListResponse> getReportsByUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        return reportService.getReportsByUserId(userDetails.getUserId(), pageable);
    }

}
