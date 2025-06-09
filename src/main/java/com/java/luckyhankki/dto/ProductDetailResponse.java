package com.java.luckyhankki.dto;

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
        LocalDateTime pickupStartDateTime,
        LocalDateTime pickupEndDateTime
        //TODO 평점, 별점, 키워드
) {
}
