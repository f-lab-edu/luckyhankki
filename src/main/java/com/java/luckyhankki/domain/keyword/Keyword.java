package com.java.luckyhankki.domain.keyword;

import jakarta.persistence.*;

@Entity
public class Keyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "keyword_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String keyword;

    protected Keyword() {}

    public Keyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getId() {
        return id;
    }

    public String getKeyword() {
        return keyword;
    }
}
