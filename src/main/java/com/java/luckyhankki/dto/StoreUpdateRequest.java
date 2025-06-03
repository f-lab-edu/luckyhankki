package com.java.luckyhankki.dto;

import java.math.BigDecimal;

public record StoreUpdateRequest(String name, String phone, String address, BigDecimal longitude, BigDecimal latitude) {
}
