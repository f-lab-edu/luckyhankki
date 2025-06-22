package com.java.luckyhankki.domain.reservation;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @EntityGraph(attributePaths = {"product"})
    List<Reservation> findAllByUserId(Long userId);

    @EntityGraph(attributePaths = {"product"})
    Reservation findByIdAndUserId(Long reservationId, Long userId);
}
