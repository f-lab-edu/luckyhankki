package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.category.Category;
import com.java.luckyhankki.domain.product.Product;
import com.java.luckyhankki.domain.product.ProductRepository;
import com.java.luckyhankki.domain.reservation.Reservation;
import com.java.luckyhankki.domain.reservation.ReservationProjection;
import com.java.luckyhankki.domain.reservation.ReservationRepository;
import com.java.luckyhankki.domain.reservation.ReservationStatus;
import com.java.luckyhankki.domain.store.Store;
import com.java.luckyhankki.domain.user.User;
import com.java.luckyhankki.domain.user.UserRepository;
import com.java.luckyhankki.dto.reservation.ReservationDetailResponse;
import com.java.luckyhankki.dto.reservation.ReservationListResponse;
import com.java.luckyhankki.dto.reservation.ReservationRequest;
import com.java.luckyhankki.dto.reservation.ReservationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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

    @Test
    @DisplayName("사용자 예약 목록 조회 성공")
    void getUserReservations_success() {
        long userId = 1L;
        User user = mock(User.class);

        Product product1 = mock(Product.class);
        when(product1.getName()).thenReturn("비빔밥");

        Product product2 = mock(Product.class);
        when(product2.getName()).thenReturn("소금빵");

        Reservation reservation1 = new Reservation(user, product1, 2);
        Reservation reservation2 = new Reservation(user, product2, 2);

        when(reservationRepository.findAllByUserId(userId))
                .thenReturn(List.of(reservation1, reservation2));

        List<ReservationListResponse> result = service.getUserReservations(userId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).productName()).isEqualTo("비빔밥");
        assertThat(result.get(1).productName()).isEqualTo("소금빵");

        verify(reservationRepository).findAllByUserId(userId);
    }

    @Test
    @DisplayName("사용자 예약 단건 조회 성공")
    void getUserReservation_success() {
        long reservationId = 1L;
        long userId = 1L;
        User user = mock(User.class);

        Product product = mock(Product.class);
        when(product.getName()).thenReturn("비빔밥");

        Reservation reservation = new Reservation(user, product, 2);
        when(reservationRepository.findByIdAndUserId(reservationId, userId))
                .thenReturn(reservation);

        ReservationDetailResponse result = service.getUserReservation(userId, reservationId);

        assertThat(result).isNotNull();
        assertThat(result.productName()).isEqualTo("비빔밥");

        verify(reservationRepository).findByIdAndUserId(reservationId, userId);
    }

    @Test
    @DisplayName("가게 예약 목록 조회 성공")
    void getStoreReservations_success() {
        // given
        Long storeId = 1L;
        ReservationProjection mockProjection = new ReservationProjection() {
            @Override public Long getId() { return 1L; }
            @Override public String getProductName() { return "소금빵"; }
            @Override public Integer getDiscountPrice() { return 3000; }
            @Override public Integer getQuantity() { return 2; }
            @Override public Integer getTotalPrice() { return 6000; }
            @Override public String getStatus() { return "CONFIRMED"; }
            @Override public LocalDateTime getCreatedAt() { return LocalDateTime.now(); }
        };

        given(reservationRepository.findAllByStoreId(storeId))
                .willReturn(List.of(mockProjection));

        // when
        List<ReservationProjection> result = service.getStoreReservations(storeId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProductName()).isEqualTo("소금빵");
        assertThat(result.get(0).getTotalPrice()).isEqualTo(6000);
    }
}