package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.product.Product;
import com.java.luckyhankki.domain.product.ProductRepository;
import com.java.luckyhankki.domain.reservation.Reservation;
import com.java.luckyhankki.domain.reservation.ReservationRepository;
import com.java.luckyhankki.domain.user.User;
import com.java.luckyhankki.domain.user.UserRepository;
import com.java.luckyhankki.dto.reservation.ReservationRequest;
import com.java.luckyhankki.dto.reservation.ReservationResponse;
import com.java.luckyhankki.exception.CustomException;
import com.java.luckyhankki.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReservationResponse reserveProduct(ReservationRequest request) {
        User user = userRepository.getReferenceById(request.userId());
        Product product = productRepository.findByIdWithLock(request.productId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        int quantity = request.quantity();
        product.decreaseStock(quantity); //재고 차감

        Reservation savedReservation = reservationRepository.save(new Reservation(user, product, quantity));

        return new ReservationResponse(savedReservation.getProduct().getName(),
                savedReservation.getQuantity(),
                savedReservation.getStatus().name());
    }
    
}
