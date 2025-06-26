package com.java.luckyhankki.domain.seller;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(indexes = {@Index(name = "unq_seller_businessnumber", columnList = "businessNumber", unique = true)})
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Transient
    private Set<SimpleGrantedAuthority> grantedAuthority = Set.of(new SimpleGrantedAuthority("ROLE_SELLER"));

    protected Seller() {}

    public Seller(String businessNumber, String name, String password, String email) {
        this.businessNumber = businessNumber;
        this.name = name;
        this.password = password;
        this.email = email;
        this.loginFail = 0;
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

    public void changePassword(String password) {
        this.password = password;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthority() {
        return grantedAuthority;
    }
}
