package com.java.luckyhankki.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String storeName,
        String categoryName,
        String name,
        int priceOriginal,
        int priceDiscount,
        int stock,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime pickupStartDateTime,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime pickupEndDateTime
) {
}
