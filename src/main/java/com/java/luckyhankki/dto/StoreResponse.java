package com.java.luckyhankki.dto;

public record StoreResponse(Long id, String name, String phone, String address, boolean isApproved, int reportCount) {
}
