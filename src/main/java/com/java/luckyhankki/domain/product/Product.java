package com.java.luckyhankki.domain.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.java.luckyhankki.domain.category.Category;
import com.java.luckyhankki.domain.store.Store;
import com.java.luckyhankki.dto.product.ProductUpdateRequest;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.function.Consumer;

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

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private int priceOriginal;

    @Column(nullable = false)
    private int priceDiscount;

    @Column(nullable = false)
    private int stock;

    private String description;

    @Column(nullable = false)
    private LocalDateTime pickupStartDateTime;

    @Column(nullable = false)
    private LocalDateTime pickupEndDateTime;

    @JsonProperty("isActive")
    @Column(nullable = false)
    private boolean isActive;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(insertable = false)
    private LocalDateTime updatedAt;

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

    public void setName(String name) {
        this.name = name;
    }

    public int getPriceOriginal() {
        return priceOriginal;
    }

    public void setPriceOriginal(int priceOriginal) {
        this.priceOriginal = priceOriginal;
    }

    public int getPriceDiscount() {
        return priceDiscount;
    }

    public void setPriceDiscount(int priceDiscount) {
        this.priceDiscount = priceDiscount;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getPickupStartDateTime() {
        return pickupStartDateTime;
    }

    public void setPickupStartDateTime(LocalDateTime pickupStartDateTime) {
        this.pickupStartDateTime = pickupStartDateTime;
    }

    public LocalDateTime getPickupEndDateTime() {
        return pickupEndDateTime;
    }

    public void setPickupEndDateTime(LocalDateTime pickupEndDateTime) {
        this.pickupEndDateTime = pickupEndDateTime;
    }

    public boolean isActive() {
        return isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     *  픽업 시간이 지났음에도 재고가 남아있는 경우 비활성화 처리
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 카테고리 변경
     *
     * @param category ProductUpdateRequest으로 들어온 새 카테고리
     */
    public void changeCategory(Category category) {
        if (!this.category.equals(category)) {
            this.category = category;
        }
    }

    /**
     * ProductUpdateRequest 필드값에 null이 아니고, 기존 Product의 값과 다른 값이 들어올 경우 업데이트
     *
     * @param request ProductUpdateRequest
     */
    public void updateProduct(ProductUpdateRequest request) {
        updateField(this::setName, request.name(), this.name);
        updateField(this::setPriceOriginal, request.priceOriginal(), this.priceOriginal);
        updateField(this::setPriceDiscount, request.priceDiscount(), this.priceDiscount);
        updateField(this::setStock, request.stock(), this.stock);
        updateField(this::setDescription, request.description(), this.description);
        updateField(this::setPickupStartDateTime, request.pickupStartDateTime(), this.pickupStartDateTime);
        updateField(this::setPickupEndDateTime, request.pickupEndDateTime(), this.pickupEndDateTime);
    }

    /**
     * 필드 업데이트 내부 메소드
     *
     * @param consumer     Product의 setter 메서드 참조
     * @param newValue     ProductUpdateRequest로 들어온 새로운 값
     * @param currentValue 현재 Product에 저장되어 있는 값
     * @param <T>          필드 타입
     */
    private <T> void updateField(Consumer<T> consumer, T newValue, T currentValue) {
        if (newValue != null && !newValue.equals(currentValue)) {
            consumer.accept(newValue);
        }
    }
}
