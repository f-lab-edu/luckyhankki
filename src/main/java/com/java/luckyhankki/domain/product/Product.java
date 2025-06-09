package com.java.luckyhankki.domain.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.java.luckyhankki.domain.category.Category;
import com.java.luckyhankki.domain.store.Store;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private String name;

    private int priceOriginal;

    private int priceDiscount;

    private int stock;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime pickupStartDateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime pickupEndDateTime;

    @JsonProperty("isActive")
    private boolean isActive;

    protected Product() {}

    public Product(Store store, Category category, String name, int priceOriginal, int priceDiscount, int stock,
                   String description, LocalDateTime pickupStartDateTime, LocalDateTime pickupEndDateTime) {
        this.store = store;
        this.category = category;
        this.name = name;
        this.priceOriginal = priceOriginal;
        this.priceDiscount = priceDiscount;
        this.stock = stock;
        this.description = description;
        this.pickupStartDateTime = pickupStartDateTime;
        this.pickupEndDateTime = pickupEndDateTime;
        this.isActive = true;
    }

    public Long getId() {
        return id;
    }

    public Store getStore() {
        return store;
    }

    public Category getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public int getPriceOriginal() {
        return priceOriginal;
    }

    public int getPriceDiscount() {
        return priceDiscount;
    }

    public int getStock() {
        return stock;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getPickupStartDateTime() {
        return pickupStartDateTime;
    }

    public LocalDateTime getPickupEndDateTime() {
        return pickupEndDateTime;
    }

    public boolean isActive() {
        return isActive;
    }

    /**
     *  픽업 시간이 지났음에도 재고가 남아있는 경우 비활성화 처리
     */
    public void deactivate() {
        this.isActive = false;
    }
}
