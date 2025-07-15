package com.java.luckyhankki.domain.keyword;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "keyword_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String keyword;

    @JsonProperty("isDeleted")
    @Column(nullable = false)
    private boolean isDeleted;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(insertable = false)
    private LocalDateTime updatedAt;

    @Version
    private Integer version;

    protected Keyword() {}

    public Keyword(String keyword) {
        this.keyword = keyword;
        this.isDeleted = false;
    }

    public Long getId() {
        return id;
    }

    public String getKeyword() {
        return keyword;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Integer getVersion() {
        return version;
    }

    /**
     * 키워드 변경 메소드
     */
    public void changeKeyword(String keyword) {
        if (!this.keyword.equals(keyword)) {
            this.keyword = keyword;
        }
    }

    /**
     * 키워드 삭제 메소드
     */
    public void deleteKeyword() {
        this.isDeleted = true;
    }

    @Override
    public String toString() {
        return "Keyword{id=%d, keyword='%s', isDeleted=%s, createdAt=%s, updatedAt=%s, version=%d}"
                .formatted(id, keyword, isDeleted, createdAt, updatedAt, version);
    }
}
