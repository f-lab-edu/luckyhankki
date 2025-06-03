package com.java.luckyhankki.domain.store;

import com.java.luckyhankki.domain.seller.Seller;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SQLDelete(sql = "UPDATE store SET is_deleted = 1 WHERE store_id = ?")
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

    @Column(nullable = false)
    private boolean isApproved;

    @Column(nullable = false)
    private int reportCount;

    @Column(nullable = false)
    private boolean isDeleted;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(insertable = false)
    private LocalDateTime updatedAt;

    protected Store() {}

    public static Store create(Seller seller, String name, String phone, String address, BigDecimal longitude, BigDecimal latitude) {
        Store store = new Store();
        store.setSeller(seller);
        store.name = name;
        store.phone = phone;
        store.address = address;
        store.longitude = longitude;
        store.latitude = latitude;
        store.isApproved = false;
        store.reportCount = 0;
        store.isDeleted = false;
        return store;
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void updateStore(String name, String phone, String address, BigDecimal longitude, BigDecimal latitude) {
        if (name != null) {
            this.name = name;
        }
        if (phone != null) {
            this.phone = phone;
        }
        if (address != null) {
            this.address = address;
        }
        if (longitude != null) {
            this.longitude = longitude;
        }
        if (latitude != null) {
            this.latitude = latitude;
        }
    }

    public void deleteStore() {
        this.isDeleted = true;
    }

}
