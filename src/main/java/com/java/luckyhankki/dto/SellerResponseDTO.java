package com.java.luckyhankki.dto;

public class SellerResponseDTO {
    private String businessNumber;
    private String name;
    private String email;

    public SellerResponseDTO() {
    }

    public SellerResponseDTO(String businessNumber, String name, String email) {
        this.businessNumber = businessNumber;
        this.name = name;
        this.email = email;
    }

    public String getBusinessNumber() {
        return businessNumber;
    }

    public void setBusinessNumber(String businessNumber) {
        this.businessNumber = businessNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
