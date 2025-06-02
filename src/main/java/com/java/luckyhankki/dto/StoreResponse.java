package com.java.luckyhankki.dto;

public record StoreResponse(String name, String phone, String address, boolean isApproved, int reportCount) {
}
