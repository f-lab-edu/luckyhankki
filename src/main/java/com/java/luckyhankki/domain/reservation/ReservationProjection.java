package com.java.luckyhankki.domain.reservation;

import java.time.LocalDateTime;

public interface ReservationProjection {
    Long getId();
    String getProductName();
    Integer getDiscountPrice();
    Integer getQuantity();
    Integer getTotalPrice();
    String getStatus();
    LocalDateTime getCreatedAt();
}
