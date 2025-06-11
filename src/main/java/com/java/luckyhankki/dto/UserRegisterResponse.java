package com.java.luckyhankki.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record UserRegisterResponse(
        Long userId,
        String name,
        String email,
        String roleType,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul") LocalDateTime createdAt
) {
}
