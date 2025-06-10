package com.java.luckyhankki.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.java.luckyhankki.config.annotation.PickupDate;
import com.java.luckyhankki.config.annotation.WonUnit;
import com.java.luckyhankki.domain.product.Product;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;
import java.util.function.Consumer;

public record ProductUpdateRequest(
        Long categoryId,

        String name,

        @PositiveOrZero(message = "원래 가격은 0원 이상이어야 합니다.")
        @WonUnit(unit = 10)
        Integer priceOriginal,

        @PositiveOrZero(message = "할인 가격은 0원 이상이어야 합니다.")
        @WonUnit(unit = 10)
        Integer priceDiscount,

        @Positive(message = "수량은 1개 이상이어야 합니다.")
        Integer stock,

        String description,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        @FutureOrPresent(message = "픽업 시작 시간은 현재 시각 이후여야 합니다.")
        @PickupDate
        LocalDateTime pickupStartDateTime,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        @FutureOrPresent(message = "픽업 종료 시간은 현재 시각 이후여야 합니다.")
        @PickupDate
        LocalDateTime pickupEndDateTime
) {
        /**
         * ProductUpdateRequest 필드값에 null이 아니고, 기존 Product의 값과 다른 값이 들어올 경우 업데이트
         *
         * @param product 현재 저장되어 있는 Product
         */
        public void updateIfPresent(Product product) {
                updateField(product::setName, name, product.getName());
                updateField(product::setPriceOriginal, priceOriginal, product.getPriceOriginal());
                updateField(product::setPriceDiscount, priceDiscount, product.getPriceDiscount());
                updateField(product::setStock, stock, product.getStock());
                updateField(product::setDescription, description, product.getDescription());
                updateField(product::setPickupStartDateTime, pickupStartDateTime, product.getPickupStartDateTime());
                updateField(product::setPickupEndDateTime, pickupEndDateTime, product.getPickupEndDateTime());
        }

        /**
         * 필드 업데이트 내부 메소드
         *
         * @param consumer     Product의 setter 메서드 참조
         * @param newValue     ProductUpdateRequest로 들어온 새로운 값
         * @param currentValue 현재 Product에 저장되어 있는 값
         * @param <T>          필드 타입
         */
        private <T> void updateField(Consumer<T> consumer, T newValue, T currentValue) {
                if (newValue != null && !newValue.equals(currentValue)) {
                        consumer.accept(newValue);
                }
        }
}
