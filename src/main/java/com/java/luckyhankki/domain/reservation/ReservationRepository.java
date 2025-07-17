package com.java.luckyhankki.domain.reservation;

import com.java.luckyhankki.dto.reservation.StoreReservationDetailResponse;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    //사용자 ID에 해당하는 모든 예약 목록들 조회
    @EntityGraph(attributePaths = {"product"})
    List<Reservation> findAllByUserId(Long userId);

    //조인 페치로 Product 함께 조회
    @Query("SELECT r FROM Reservation r JOIN FETCH r.product WHERE r.id = :reservationId AND r.user.id = :userId")
    Reservation findByIdAndUserIdWithProduct(@Param("reservationId") Long reservationId, @Param("userId") Long userId);

    //예약 ID와 사용자 ID에 해당하는 예약 단건 조회
    Reservation findByIdAndUserId(Long reservationId, Long userId);

    //가게 ID에 해당하는 모든 예약 목록 조회
    @Query("SELECT r.id AS id, p.name AS productName, p.priceDiscount AS discountPrice, " +
            "r.quantity AS quantity, (p.priceDiscount * r.quantity) AS totalPrice, r.status AS status, r.createdAt AS createdAt " +
            "FROM Reservation r JOIN r.product p " +
            "WHERE p.store.id = :storeId AND r.status IN ('CONFIRMED', 'COMPLETED') " +
            "ORDER BY r.id DESC")
    List<ReservationProjection> findAllByStoreId(@Param("storeId") Long storeId);

    //가게 ID와 예약 ID에 해당하는 예약 내역 상세 조회
    @Query("SELECT new com.java.luckyhankki.dto.reservation.StoreReservationDetailResponse(" +
            "r.id, p.name, u.name, SUBSTRING(u.phone, 8, 4), r.quantity, p.priceOriginal, p.priceDiscount, " +
            "(p.priceDiscount * r.quantity) AS totalPrice, p.pickupStartDateTime, p.pickupEndDateTime, r.status, r.createdAt) " +
            "FROM Reservation r JOIN r.product p JOIN r.user u " +
            "WHERE r.id = :reservationId AND p.store.id = :storeId " +
            "ORDER BY r.id DESC")
    StoreReservationDetailResponse findByIdAndProductStoreId(@Param("reservationId") Long reservationId,
                                                             @Param("storeId") Long storeId);

    //사용자 ID와 상품 ID에 해당하는 예약 건의 예약 상태 조회
    @Query("SELECT r.status AS status FROM Reservation r JOIN r.product p " +
            "WHERE r.user.id = :userId AND p.id = :productId")
    Optional<ReservationStatusProjection> findByUserIdAndProductId(@Param("userId") Long userId,
                                                                   @Param("productId") Long productId);
}
