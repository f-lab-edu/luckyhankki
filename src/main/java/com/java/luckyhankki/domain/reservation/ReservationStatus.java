package com.java.luckyhankki.domain.reservation;

/**
 * 예약 상태 ENUM 클래스
 * TODO 결제 관련 기능 구현은 나중으로 미뤄둔다. 현재는 바로 CONFIRMED로 구현
 */
public enum ReservationStatus {
    PENDING,      // 결제 대기 (초기 예약, 30분 내 결제 필요)
    CONFIRMED,    // 결제 완료 (가게에서 확인 가능)
    COMPLETED,    // 픽업 완료 (가게에서 확인 가능)
    CANCELLED     // 취소 (사용자 취소 또는 30분 초과 자동 취소)
}
