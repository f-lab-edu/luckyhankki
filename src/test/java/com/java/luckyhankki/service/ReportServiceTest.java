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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @InjectMocks
    private ReportService reportService;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Test
    @DisplayName("신고 작성 성공")
    void addReport_success() {
        Long userId = 1L;
        Long reservationId = 1L;
        String content = "픽업한 상품이 상품 소개와 달라서 신고합니다.";

        Reservation reservation = mock(Reservation.class);
        ReportRequest request = new ReportRequest(reservationId, content);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(reservation.getStatus()).thenReturn(ReservationStatus.COMPLETED);
        when(reservation.getId()).thenReturn(reservationId);
        when(reportRepository.save(any(Report.class))).then(returnsFirstArg());

        ReportResponse report = reportService.addReport(userId, request);

        assertEquals(userId, report.userId());
        assertEquals(reservationId, report.reservationId());
        assertEquals(content, report.content());

        verify(reservationRepository).findById(reservationId);
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    @DisplayName("신고 작성 실패 - 예약을 찾을 수 없을 때 예외 발생")
    void addReport_throwsException_whenReservationNotFound() {
        Long userId = 1L;
        Long reservationId = 1L;
        String content = "픽업한 상품이 상품 소개와 달라서 신고합니다.";

        ReportRequest request = new ReportRequest(reservationId, content);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> reportService.addReport(userId, request));

        assertEquals("예약 내역이 존재하지 않습니다.", exception.getMessage());

        verify(reservationRepository).findById(reservationId);
    }

    @Test
    @DisplayName("신고 작성 실패 - 예약 상태가 COMPLETED가 아닐 때 예외 발생")
    void addReport_throwsException_whenStatusIsNotCompleted() {
        Long userId = 1L;
        Long reservationId = 1L;
        String content = "픽업한 상품이 상품 소개와 달라서 신고합니다.";

        Reservation reservation = mock(Reservation.class);
        ReportRequest request = new ReportRequest(reservationId, content);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(reservation.getStatus()).thenReturn(ReservationStatus.PENDING);

        CustomException exception = assertThrows(CustomException.class, () -> reportService.addReport(userId, request));

        assertEquals("픽업이 완료된 후에 작성할 수 있습니다.", exception.getMessage());

        verify(reservationRepository).findById(reservationId);
    }

    @Test
    @DisplayName("신고 목록 조회 성공")
    void getReportsByUserId_success() {
        Long userId = 1L;
        ReportListResponse report1 = mock(ReportListResponse.class);
        ReportListResponse report2 = mock(ReportListResponse.class);

        List<ReportListResponse> reportList = List.of(report1, report2);
        Pageable pageable = PageRequest.of(0, 10);
        Slice<ReportListResponse> slice = new SliceImpl<>(reportList, pageable, false);

        when(reportRepository.findAllByUserId(eq(userId), eq(pageable))).thenReturn(slice);

        Slice<ReportListResponse> result = reportService.getReportsByUserId(userId, pageable);

        assertNotNull(result);
        assertThat(result).hasSize(2);

        verify(reportRepository).findAllByUserId(eq(userId), eq(pageable));
    }
}