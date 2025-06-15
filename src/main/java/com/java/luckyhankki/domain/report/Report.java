package com.java.luckyhankki.domain.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.java.luckyhankki.domain.reservation.Reservation;
import com.java.luckyhankki.domain.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String content;

    @JsonProperty("isCompleted")
    @Column(nullable = false)
    private boolean isCompleted;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(insertable = false)
    private LocalDateTime updatedAt;

    protected Report() {}

    public Long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public User getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
