package com.java.luckyhankki.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SellerRequestDTO {

    @Pattern(regexp = "^[0-9]{10}$", message = "사업자 등록번호는 10자리 숫자만 입력 가능합니다.")
    private String businessNumber;

    @NotBlank(message = "사업자 대표성명은 반드시 입력해야 합니다.")
    @Size(min = 2)
    private String name;

    @NotBlank
    private String password;

    @Email
    private String email;

    public SellerRequestDTO(String businessNumber, String name, String password, String email) {
        this.businessNumber = businessNumber;
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public String getBusinessNumber() {
        return businessNumber;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
