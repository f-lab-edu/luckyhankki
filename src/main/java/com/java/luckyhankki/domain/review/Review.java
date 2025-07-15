package com.java.luckyhankki.domain.review;

import com.java.luckyhankki.domain.keyword.Keyword;
import com.java.luckyhankki.domain.product.Product;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 리뷰는 최소 1개의 키워드를 선택해야한다.
 * 별점은 1점부터 5점까지 선택해야 한다.
 * 리뷰 내용 작성은 필수가 아니다.
 */
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "review_keyword",
            joinColumns = @JoinColumn(name = "review_id"),
            inverseJoinColumns = @JoinColumn(name = "keyword_id")
    )
    private List<Keyword> keywords = new ArrayList<>();

    @Column(nullable = false)
    private int ratingScore;    //1~5점

    private String content;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(insertable = false)
    private LocalDateTime updatedAt;

    protected Review() {}

    public Review(long userId, Product product, List<Keyword> keywords, int ratingScore, String content) {
        this.userId = userId;
        this.product = product;
        this.keywords = keywords;
        this.ratingScore = ratingScore;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Product getProduct() {
        return product;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public int getRatingScore() {
        return ratingScore;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Review{id=%d, userId=%d, ratingScore=%d, content='%s', createdAt=%s, updatedAt=%s}"
                .formatted(id, userId, ratingScore, content, createdAt, updatedAt);
    }
}
