package com.java.luckyhankki.domain.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @Column(nullable = false)
    private Long reservationId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String content;

    @JsonProperty("isCompleted")
    @Column(nullable = false)
    private boolean isCompleted;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;

    @Column
    private String adminMemo;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(insertable = false)
    private LocalDateTime updatedAt;

    protected Report() {}

    public Report(Long reservationId, Long userId, String content) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.content = content;
        this.isCompleted = false;
        this.status = ReportStatus.PENDING;
    }

    public Long getId() {
        return id;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public String getAdminMemo() {
        return adminMemo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 관리자 신고 처리
     * @param status    신고 상태(REJECTED or RESOLVED)
     * @param adminMemo 처리 내용 메모
     */
    public void handleReport(ReportStatus status, String adminMemo) {
        this.isCompleted = true;
        this.status = status;
        this.adminMemo = adminMemo;
    }

    @Override
    public String toString() {
        return "Report{id=%d, reservationId=%d, userId=%d, content='%s', isCompleted=%s, status=%s, adminMemo='%s', createdAt=%s, updatedAt=%s}"
                .formatted(id, reservationId, userId, content, isCompleted, status, adminMemo, createdAt, updatedAt);
    }
}
