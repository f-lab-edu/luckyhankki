package com.java.luckyhankki.domain.report;

import com.java.luckyhankki.domain.store.Store;
import com.java.luckyhankki.dto.admin.AdminReportListResponse;
import com.java.luckyhankki.dto.report.ReportListResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    Slice<ReportListResponse> findAllByUserId(Long userId, Pageable pageable);

    @Query("""
        SELECT p.store
        FROM Report r
        JOIN Reservation res ON r.reservationId = res.id
        JOIN Product p ON res.product.id = p.id
        WHERE r.id = :id
    """)
    Optional<Store> findStoreById(@Param("id") Long id);

    Slice<AdminReportListResponse> findAllByIsCompletedFalse(Pageable pageable);
}
