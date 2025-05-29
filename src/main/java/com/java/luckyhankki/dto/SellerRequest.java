package com.java.luckyhankki.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SellerRequest(
        @Pattern(
                regexp = "^[0-9]{10}$",
                message = "사업자 등록번호는 10자리 숫자만 입력 가능합니다."
        )
        String businessNumber,

        @NotBlank(message = "사업자 대표성명은 반드시 입력해야 합니다.")
        @Size(min = 2)
        String name,

        @NotBlank
        String password,

        @Email
        String email
) {
}
