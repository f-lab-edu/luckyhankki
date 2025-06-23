package com.java.luckyhankki.domain.reservation;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @EntityGraph(attributePaths = {"product"})
    List<Reservation> findAllByUserId(Long userId);

    @EntityGraph(attributePaths = {"product"})
    Reservation findByIdAndUserId(Long reservationId, Long userId);

    @Query("SELECT r.id AS id, r.product.name AS productName, r.product.priceDiscount AS discountPrice," +
            "r.quantity AS quantity, (r.product.priceDiscount * r.quantity) AS totalPrice, r.status AS status, r.createdAt AS createdAt " +
            "FROM Reservation r JOIN r.product " +
            "WHERE r.product.store.id = :storeId AND r.status IN ('CONFIRMED', 'COMPLETED')")
    List<ReservationProjection> findAllByStoreId(@Param("storeId") Long storeId);

    @EntityGraph(attributePaths = {"product", "user"})
    Optional<Reservation> findByIdAndProductStoreId(Long reservationId, Long storeId);

}
