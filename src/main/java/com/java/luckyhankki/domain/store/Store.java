package com.java.luckyhankki.domain.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.java.luckyhankki.domain.seller.Seller;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 100)
    private String address;

    //경도(X, 126.XXXXXX)
    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal longitude;

    //위도(Y, 36.XXXXXX)
    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal latitude;

    @JsonProperty("isApproved")
    @Column(nullable = false)
    private boolean isApproved;

    @Column(nullable = false)
    private int reportCount;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(insertable = false)
    private LocalDateTime updatedAt;

    @JsonProperty("isActive")
    @Column(nullable = false)
    private boolean isActive;

    protected Store() {}

    public Store(Seller seller, String name, String phone, String address, BigDecimal longitude, BigDecimal latitude) {
        this.seller = seller;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.isApproved = false;
        this.reportCount = 0;
        this.isActive = false;
    }

    public Long getId() {
        return id;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public int getReportCount() {
        return reportCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isActive() {
        return isActive;
    }

    /**
     * 관리자가 가게를 승인하는 메소드
     */
    public void approveStore() {
        this.isApproved = true;
        this.isActive = true;
    }

    /**
     * 경고 누적 처리
     */
    public void addReport() {
        this.reportCount++;
        if (this.reportCount >= 3) {
            deactivate();
        }
    }

    /**
     * 경고 3회 누적 시 비활성화 처리
     */
    private void deactivate() {
        this.isActive = false;
    }

    @Override
    public String toString() {
        return "Store{id=%d, name='%s', phone='%s', address='%s', longitude=%s, latitude=%s, isApproved=%s, reportCount=%d, createdAt=%s, updatedAt=%s, isDeleted=%s}"
                .formatted(id, name, phone, address, longitude, latitude, isApproved, reportCount, createdAt, updatedAt, isActive);
    }
}
