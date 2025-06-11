package com.java.luckyhankki.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ProductDetailResponse(
        String storeName,
        String storeAddress,
        String storePhone,
        String categoryName,
        String productName,
        String description,
        int stock,
        int priceOriginal,
        int priceDiscount,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime pickupStartDateTime,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime pickupEndDateTime
        //TODO 평점, 별점, 키워드
) {
}
