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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @BeforeEach
    void setUp() {
        // 사용자 생성
        user = new User("user@test.com", "홍길동", "010-1111-2222",
                "경기도 수원시 XX구 XX동", BigDecimal.valueOf(37.123456), BigDecimal.valueOf(126.123456));
        user.changePassword("abc456#!@");
        user = userRepository.save(user);

        // 판매자, 매장, 카테고리, 상품 생성
        Seller seller = new Seller("1234567890", "김사장", "abc#@123", "ceo@test.com");
        seller = sellerRepository.save(seller);

        Store store = new Store(seller, "길동국밥", "031-111-2222",
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
        Reservation reservation = new Reservation(user, product, 2);

        Reservation saved = reservationRepository.save(reservation);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUser().getId()).isEqualTo(user.getId());
        assertThat(saved.getProduct().getId()).isEqualTo(product.getId());
        assertThat(saved.getQuantity()).isEqualTo(2);
        assertThat(saved.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(saved.getCreatedAt()).isNotNull();
    }
}