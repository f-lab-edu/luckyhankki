package com.java.luckyhankki.domain.report;

import com.java.luckyhankki.domain.category.Category;
import com.java.luckyhankki.domain.category.CategoryRepository;
import com.java.luckyhankki.domain.product.Product;
import com.java.luckyhankki.domain.product.ProductRepository;
import com.java.luckyhankki.domain.reservation.Reservation;
import com.java.luckyhankki.domain.reservation.ReservationRepository;
import com.java.luckyhankki.domain.seller.Seller;
import com.java.luckyhankki.domain.seller.SellerRepository;
import com.java.luckyhankki.domain.store.Store;
import com.java.luckyhankki.domain.store.StoreRepository;
import com.java.luckyhankki.domain.user.User;
import com.java.luckyhankki.domain.user.UserRepository;
import com.java.luckyhankki.dto.admin.AdminReportListResponse;
import com.java.luckyhankki.dto.report.ReportListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class ReportRepositoryTest {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    @DisplayName("사용자가 작성한 신고 목록 조회")
    void testFindAllByUserId() {
        Long reservationId = 1L;
        Long userId = 1L;
        String content = "신고합니다.";

        Report report = new Report(reservationId, userId, content);
        reportRepository.save(report);

        Pageable pageable = PageRequest.of(0, 10);

        Slice<ReportListResponse> reports = reportRepository.findAllByUserId(userId, pageable);

        assertThat(reports).isNotEmpty();
        assertThat(reports.getContent().get(0).isCompleted()).isFalse();
    }

    @Test
    @DisplayName("신고 ID로 가게 정보 조회")
    void findStoreById() {
        Seller seller = new Seller("1234567890", "김사장", "abc#@123", "ceo@test.com");
        seller = sellerRepository.save(seller);

        Store store = new Store(seller, "길동국밥", "031-111-2222",
                "경기도 수원시 YY구 YY동", BigDecimal.valueOf(37.777666), BigDecimal.valueOf(126.777666));
        store = storeRepository.save(store);

        Category category = new Category("음식");
        category = categoryRepository.save(category);

        Product product = new Product(store, category, "비빔밥", 10000, 8000, 10,
                "육회비빔밥 입니다.", LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2));
        product = productRepository.save(product);

        User user = new User("user@test.com", "홍길동", "01011112222",
                "경기도 수원시 XX구 XX동", BigDecimal.valueOf(37.123456), BigDecimal.valueOf(126.123456));
        user.changePassword("abc456#!@");
        user = userRepository.save(user);

        Reservation reservation = new Reservation(user, product, 2);
        reservation = reservationRepository.save(reservation);

        Report report = new Report(reservation.getId(), user.getId(), "신고합니다.");
        report = reportRepository.save(report);

        Optional<Store> optionalStore = reportRepository.findStoreById(report.getId());
        assertThat(optionalStore.isPresent()).isTrue();

        Store result = optionalStore.get();
        assertThat(result.getId()).isEqualTo(store.getId());
    }

    @Test
    @DisplayName("조치 미완료된 신고 목록 조회")
    void testFindAllByIsCompletedFalse() {
        Long reservationId = 1L;
        Long userId = 1L;
        String content = "신고합니다.";

        Report report = new Report(reservationId, userId, content);
        reportRepository.save(report);

        Slice<AdminReportListResponse> result = reportRepository.findAllByIsCompletedFalse(PageRequest.of(0, 10));

        assertThat(result).isNotEmpty();
        assertEquals(reservationId, result.getContent().get(0).reservationId());
    }

}