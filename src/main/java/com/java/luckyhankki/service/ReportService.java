package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.report.Report;
import com.java.luckyhankki.domain.report.ReportRepository;
import com.java.luckyhankki.domain.reservation.Reservation;
import com.java.luckyhankki.domain.reservation.ReservationRepository;
import com.java.luckyhankki.domain.reservation.ReservationStatus;
import com.java.luckyhankki.dto.report.ReportListResponse;
import com.java.luckyhankki.dto.report.ReportRequest;
import com.java.luckyhankki.dto.report.ReportResponse;
import com.java.luckyhankki.exception.CustomException;
import com.java.luckyhankki.exception.ErrorCode;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReservationRepository reservationRepository;

    public ReportService(ReportRepository reportRepository, ReservationRepository reservationRepository) {
        this.reportRepository = reportRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public ReportResponse addReport(Long userId, ReportRequest request) {
        Reservation reservation = reservationRepository.findById(request.reservationId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if (reservation.getStatus() != ReservationStatus.COMPLETED) {
            throw new CustomException(ErrorCode.ACTION_NOT_ALLOWED_BEFORE_PICKUP);
        }

        Report report = new Report(reservation.getId(), userId, request.content());
        Report savedReport = reportRepository.save(report);

        return new ReportResponse(
                savedReport.getId(),
                savedReport.getUserId(),
                savedReport.getReservationId(),
                savedReport.getContent());
    }

    @Transactional(readOnly = true)
    public Slice<ReportListResponse> getReportsByUserId(Long userId, Pageable pageable) {
        return reportRepository.findAllByUserId(userId, pageable);
    }
}
