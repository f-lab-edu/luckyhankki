package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.category.Category;
import com.java.luckyhankki.domain.product.Product;
import com.java.luckyhankki.domain.product.ProductRepository;
import com.java.luckyhankki.domain.reservation.Reservation;
import com.java.luckyhankki.domain.reservation.ReservationRepository;
import com.java.luckyhankki.domain.reservation.ReservationStatus;
import com.java.luckyhankki.domain.store.Store;
import com.java.luckyhankki.domain.user.User;
import com.java.luckyhankki.domain.user.UserRepository;
import com.java.luckyhankki.dto.reservation.ReservationRequest;
import com.java.luckyhankki.dto.reservation.ReservationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @InjectMocks
    private ReservationService service;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("상품 예약 성공")
    void reserveProduct_success() {
        long productId = 1L;
        long userId = 1L;
        int stock = 10;
        int quantity = 3;

        User user = mock(User.class);
        Store store = mock(Store.class);
        Category category = mock(Category.class);
        Product product = new Product(
                store,
                category,
                "비빔밥",
                10000,
                8000,
                stock,
                "육회비빔밥 입니다.",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        ReservationRequest request = new ReservationRequest(productId, userId, quantity);

        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(productRepository.findByIdWithLock(productId)).thenReturn(Optional.of(product));
        when(reservationRepository.save(any(Reservation.class))).then(returnsFirstArg());

        ReservationResponse reservation = service.reserveProduct(request);

        assertThat(reservation.productName()).isEqualTo("비빔밥");
        assertThat(reservation.quantity()).isEqualTo(quantity);
        assertThat(reservation.status()).isEqualTo(ReservationStatus.CONFIRMED.name());
        assertThat(product.getStock()).isEqualTo(stock - quantity);

        verify(userRepository, times(1)).getReferenceById(userId);
        verify(productRepository).findByIdWithLock(productId);
        verify(reservationRepository).save(any(Reservation.class));
    }
}