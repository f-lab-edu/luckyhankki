package com.java.luckyhankki.domain.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    //ID : 이메일
    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @JsonProperty(access = WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 10)
    private String name;

    @Column(nullable = false,  length = 20)
    private String phone;

    @Column(nullable = false, length = 100)
    private String address;

    //경도(X, 126.XXXXXX)
    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal longitude;

    //위도(Y, 36.XXXXXX)
    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal latitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private RoleType roleType;

    @Column(nullable = false)
    private int loginFailCnt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(insertable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime lastLoginAt;

    @JsonProperty("isDeleted")
    @Column(nullable = false)
    private boolean isDeleted;

    protected User() {}

    public User(String email, String name, String phone, String address, BigDecimal longitude, BigDecimal latitude) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.roleType = RoleType.CUSTOMER;
        this.loginFailCnt = 0;
        this.isDeleted = false;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
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

    public RoleType getRoleType() {
        return roleType;
    }

    public int getLoginFailCnt() {
        return loginFailCnt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    /**
     * 비밀번호 암호화 메소드
     * TODO 추후에 PasswordEncoer 사용해서 암호화 처리
     *
     * @param password          평문으로 된 비밀번호
     */
    public void changePassword(String password) {
        this.password = password;
    }

    /**
     * 로그인 시 로그인 한 현재 시각 저장 메소드
     */
    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }
}
