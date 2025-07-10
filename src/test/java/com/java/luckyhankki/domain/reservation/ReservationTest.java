package com.java.luckyhankki.domain.reservation;

import com.java.luckyhankki.domain.category.Category;
import com.java.luckyhankki.domain.category.CategoryRepository;
import com.java.luckyhankki.domain.product.Product;
import com.java.luckyhankki.domain.product.ProductRepository;
import com.java.luckyhankki.domain.seller.Seller;
import com.java.luckyhankki.domain.seller.SellerRepository;
import com.java.luckyhankki.domain.store.Store;
import com.java.luckyhankki.domain.store.StoreRepository;
import com.java.luckyhankki.domain.user.User;
import com.java.luckyhankki.domain.user.UserRepository;
import com.java.luckyhankki.dto.reservation.StoreReservationDetailResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ReservationTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User user;
    private Product product;
    private Store store;

    @BeforeEach
    void setUp() {
        // 사용자 생성
        user = new User("user@test.com", "홍길동", "01011112222",
                "경기도 수원시 XX구 XX동", BigDecimal.valueOf(37.123456), BigDecimal.valueOf(126.123456));
        user.changePassword("abc456#!@");
        user = userRepository.save(user);

        // 판매자, 매장, 카테고리, 상품 생성
        Seller seller = new Seller("1234567890", "김사장", "abc#@123", "ceo@test.com");
        seller = sellerRepository.save(seller);

        store = new Store(seller, "길동국밥", "031-111-2222",
                "경기도 수원시 YY구 YY동", BigDecimal.valueOf(37.777666), BigDecimal.valueOf(126.777666));
        store = storeRepository.save(store);

        Category category = new Category("음식");
        category = categoryRepository.save(category);

        product = new Product(store, category, "비빔밥", 10000, 8000, 10,
                "육회비빔밥 입니다.", LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2));
        product = productRepository.save(product);
    }

    @Test
    @DisplayName("예약 저장 테스트")
    public void save() {
        Reservation saved = getReservation();

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUser().getId()).isEqualTo(user.getId());
        assertThat(saved.getProduct().getId()).isEqualTo(product.getId());
        assertThat(saved.getQuantity()).isEqualTo(2);
        assertThat(saved.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("사용자가 예약한 예약목록 조회")
    public void findAllByUserId() {
        getReservation();
        List<Reservation> reservations = reservationRepository.findAllByUserId(user.getId());

        assertThat(reservations).isNotEmpty();
    }

    @Test
    @DisplayName("사용자가 예약한 예약ID에 해당하는 예약 단건 조회")
    public void findByUserIdAndId() {
        Reservation reservation = getReservation();
        Reservation findReservation = reservationRepository.findByIdAndUserId(reservation.getId(), user.getId());

        assertThat(findReservation).isNotNull();
    }

    @Test
    @DisplayName("가게에 등록된 모든 예약 내역 조회")
    public void findAllByStoreId() {
        Reservation reservation1 = getReservation();
        Reservation reservation2 = getReservation();
        reservation2.setStatus(ReservationStatus.CANCELLED);

        List<ReservationProjection> reservations = reservationRepository.findAllByStoreId(store.getId());

        assertThat(reservations).isNotEmpty();
        assertThat(reservations).hasSize(1);
        assertThat(reservations.get(0).getStatus()).isEqualTo("CONFIRMED");
        assertThat(reservations.get(0).getProductName()).isEqualTo(product.getName());
        assertThat(reservations.get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("가게ID와 예약ID로 예약 내역 상세 조회")
    public void findByIdAndProductStoreId() {
        Reservation reservation = getReservation();

        StoreReservationDetailResponse optional = reservationRepository.findByIdAndProductStoreId(reservation.getId(), store.getId());

        assertThat(optional).isNotNull();
        assertThat(optional.userPhone()).isEqualTo("2222");
    }

    @Test
    @DisplayName("유저ID와 상품ID로 예약 상태 조회")
    public void findByUserIdAndProductId() {
        Reservation reservation = getReservation();
        reservation.setStatus(ReservationStatus.CONFIRMED);

        ReservationStatusProjection statusProjection =
                reservationRepository.findByUserIdAndProductId(user.getId(), product.getId()).get();
        assertThat(statusProjection).isNotNull();
        assertThat(statusProjection.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    private Reservation getReservation() {
        Reservation reservation = new Reservation(user, product, 2);

        return reservationRepository.save(reservation);
    }
}