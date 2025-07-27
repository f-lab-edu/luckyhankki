package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.product.Product;
import com.java.luckyhankki.domain.product.ProductRepository;
import com.java.luckyhankki.domain.reservation.Reservation;
import com.java.luckyhankki.domain.reservation.ReservationProjection;
import com.java.luckyhankki.domain.reservation.ReservationRepository;
import com.java.luckyhankki.domain.reservation.ReservationStatus;
import com.java.luckyhankki.domain.store.StoreProjection;
import com.java.luckyhankki.domain.store.StoreRepository;
import com.java.luckyhankki.domain.user.User;
import com.java.luckyhankki.domain.user.UserRepository;
import com.java.luckyhankki.dto.reservation.*;
import com.java.luckyhankki.exception.CustomException;
import com.java.luckyhankki.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);
    private final ReservationRepository reservationRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    public ReservationService(ReservationRepository reservationRepository, ProductRepository productRepository, UserRepository userRepository, StoreRepository storeRepository) {
        this.reservationRepository = reservationRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
    }

    @Transactional
    public ReservationResponse reserveProduct(Long userId, ReservationRequest request) {
        User user = userRepository.getReferenceById(userId);
        Product product = productRepository.findByIdWithLock(request.productId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        int quantity = request.quantity();
        product.decreaseStock(quantity); //재고 차감

        Reservation savedReservation = reservationRepository.save(new Reservation(user, product, quantity));

        return new ReservationResponse(savedReservation.getProduct().getName(),
                savedReservation.getQuantity(),
                savedReservation.getStatus().name());
    }

    /**
     * 사용자 ID에 해당하는 모든 예약 목록들 조회
     */
    @Transactional(readOnly = true)
    public List<ReservationListResponse> getReservationListByUser(Long userId) {
        List<Reservation> reservations = reservationRepository.findAllByUserId(userId);

        return reservations.stream()
                .map(r -> new ReservationListResponse(
                        r.getProduct().getName(),
                        r.getProduct().getPriceDiscount(),
                        r.getStatus().name(),
                        r.getCreatedAt()))
                .toList();
    }

    /**
     * 사용자 ID, 예약ID에 해당하는 단건 예약 조회
     * TODO 가게명, 가게주소도 응답
     */
    @Transactional(readOnly = true)
    public ReservationDetailResponse getReservationByUser(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findByIdAndUserIdWithProduct(reservationId, userId);
        Product product = reservation.getProduct();

        return new ReservationDetailResponse(product.getName(),
                product.getPriceOriginal(),
                product.getPriceDiscount(),
                product.getPickupStartDateTime(),
                product.getPickupEndDateTime(),
                reservation.getQuantity(),
                reservation.getStatus().name(),
                reservation.getCreatedAt());
    }

    /**
     * 사용자 예약 취소
     */
    @Transactional
    public void cancelReservationByUser(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findByIdAndUserId(reservationId, userId);
        log.info("[cancelReservationByUser] reservationId: {}, userId: {}", reservationId, userId);
        if (reservation.getStatus() == ReservationStatus.CANCELLED || reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new CustomException(ErrorCode.RESERVATION_CANCEL_FAILED);
        }

        reservation.setStatus(ReservationStatus.CANCELLED);

        //비관적 락으로 상품 조회
        Product product = productRepository.findByIdWithLock(reservation.getProduct().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        log.info("[cancelReservationByUser] before increase productStock: {}", product.getStock());

        //재고 증가
        product.increaseStock(reservation.getQuantity());
        log.info("[cancelReservationByUser] after increase productStock: {}", product.getStock());
    }

    /**
     * 가게의 모든 예약 목록 중 예약 상태가 CONFIRMED와 COMPLETED인 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ReservationProjection> getReservationListByStore(Long sellerId) {
        Long storeId = storeRepository.findStoreBySellerId(sellerId)
                .map(StoreProjection::getId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        return reservationRepository.findAllByStoreId(storeId);
    }

    /**
     * 가게에 예약 내역 상세 조회
     */
    @Transactional(readOnly = true)
    public StoreReservationDetailResponse getReservationDetailsByStore(Long reservationId, Long sellerId) {
        Long storeId = storeRepository.findStoreBySellerId(sellerId)
                .map(StoreProjection::getId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        return reservationRepository.findByIdAndProductStoreId(reservationId, storeId);
    }

    /**
     * 픽업 처리
     */
    @Transactional
    public void updateStatusCompleted(Long reservationId, Long sellerId) {
        Reservation reservation = reservationRepository.findByIdAndSellerId(reservationId, sellerId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        reservation.setStatus(ReservationStatus.COMPLETED);
    }
}
