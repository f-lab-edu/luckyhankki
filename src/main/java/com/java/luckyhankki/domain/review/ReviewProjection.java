package com.java.luckyhankki.domain.review;

import com.java.luckyhankki.domain.keyword.Keyword;

import java.time.LocalDateTime;
import java.util.List;

public interface ReviewProjection {
    String getProductName();
    List<Keyword> getKewords();
    Integer getRatingScore();
    String getContent();
    LocalDateTime getCreatedAt();
}
