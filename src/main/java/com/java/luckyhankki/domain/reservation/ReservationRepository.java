package com.java.luckyhankki.domain.reservation;

import com.java.luckyhankki.dto.reservation.StoreReservationDetailResponse;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @EntityGraph(attributePaths = {"product"})
    List<Reservation> findAllByUserId(Long userId);

    @EntityGraph(attributePaths = {"product"})
    Reservation findByIdAndUserId(Long reservationId, Long userId);

    @Query("SELECT r.id AS id, p.name AS productName, p.priceDiscount AS discountPrice, " +
            "r.quantity AS quantity, (p.priceDiscount * r.quantity) AS totalPrice, r.status AS status, r.createdAt AS createdAt " +
            "FROM Reservation r JOIN r.product p " +
            "WHERE p.store.id = :storeId AND r.status IN ('CONFIRMED', 'COMPLETED') " +
            "ORDER BY r.id DESC")
    List<ReservationProjection> findAllByStoreId(@Param("storeId") Long storeId);

    @Query("SELECT new com.java.luckyhankki.dto.reservation.StoreReservationDetailResponse(" +
            "r.id, p.name, u.name, SUBSTRING(u.phone, 8, 4), r.quantity, p.priceOriginal, p.priceDiscount, " +
            "(p.priceDiscount * r.quantity) AS totalPrice, p.pickupStartDateTime, p.pickupEndDateTime, r.status, r.createdAt) " +
            "FROM Reservation r JOIN r.product p JOIN r.user u " +
            "WHERE r.id = :reservationId AND p.store.id = :storeId " +
            "ORDER BY r.id DESC")
    StoreReservationDetailResponse findByIdAndProductStoreId(@Param("reservationId") Long reservationId,
                                                             @Param("storeId") Long storeId);

}
