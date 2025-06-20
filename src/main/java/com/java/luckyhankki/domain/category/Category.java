package com.java.luckyhankki.domain.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Schema(description = "카테고리")
@Entity
public class Category {

    @Schema(description = "카테고리 ID", example = "1")
    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    @Schema(description = "카테고리명", example = "음식")
    @Column(length = 10, nullable = false, unique = true)
    private String name;

    protected Category() {}

    public Category(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void changeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("카테고리명은 비어 있을 수 없습니다.");
        }
        this.name = name;
    }
}
