package com.java.luckyhankki.domain.seller;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
public class Seller {

    @Id @GeneratedValue
    @Column(name = "seller_id")
    private Long id;

    @Column(nullable = false, length = 10)
    private String businessNumber;

    @Column(nullable = false, length = 10)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false)
    private int loginFail;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(insertable = false)
    private LocalDateTime updatedAt;

    protected Seller() {}

    public static Seller create(String businessNumber, String name, String password, String email) {
        Seller seller = new Seller();
        seller.businessNumber = businessNumber;
        seller.name = name;
        seller.password = password;
        seller.email = email;
        seller.loginFail = 0;
        return seller;
    }

    public Long getId() {
        return id;
    }

    public String getBusinessNumber() {
        return businessNumber;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public int getLoginFail() {
        return loginFail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
