package com.java.luckyhankki.integration;

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
import com.java.luckyhankki.dto.reservation.ReservationRequest;
import com.java.luckyhankki.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class ReservationIntegrationTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StoreRepository storeRepository;

    private Long productId;
    private Long userId;

    @BeforeEach
    public void setup() {
        reservationRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        storeRepository.deleteAllInBatch();
        sellerRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        User user = new User("user@test.com",
                "홍길동",
                "010-1111-2222",
                "경기도 수원시 XX구 XX동",
                BigDecimal.valueOf(37.123456),
                BigDecimal.valueOf(126.123456));
        user.changePassword("abc456#!@");

        Seller seller = new Seller("1234567890",
                "김사장",
                "abc#@123",
                "ceo@test.com");

        Store store = new Store(seller,
                "길동국밥",
                "031-111-2222",
                "경기도 수원시 YY구 YY동",
                BigDecimal.valueOf(37.777666),
                BigDecimal.valueOf(126.777666));

        Category category = new Category("음식");

        Product product = new Product(store,
                category,
                "비빔밥",
                10000,
                8000,
                10,
                "육회비빔밥 입니다.",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2));

        userId = userRepository.saveAndFlush(user).getId();
        sellerRepository.saveAndFlush(seller);
        storeRepository.saveAndFlush(store);
        categoryRepository.saveAndFlush(category);
        productId = productRepository.saveAndFlush(product).getId();
    }

    @Test
    @DisplayName("재고 감소 동시성 테스트 - 재고 10, 요청 5*1=5")
    void concurrencyTest_success() throws InterruptedException {
        int threadCount = 5;
        int quantityPerRequest = 1;

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        concurrentTest(threadCount, quantityPerRequest, successCount, failureCount);

        Product product = productRepository.findById(productId).orElseThrow();
        
        assertThat(product.getStock()).isEqualTo(5); // 10 - 5 = 5
        assertThat(successCount.get()).isEqualTo(5); // 5 * 1 = 5
        assertThat(failureCount.get()).isEqualTo(0);
    }

    @Test
    @DisplayName("초과 주문 테스트 - 재고 5, 요청 10*2=20")
    void overRequestTest() throws InterruptedException {
        int stock = 5;
        int threadCount = 10;
        int quantityPerRequest = 2;

        Product product = productRepository.findById(productId).orElseThrow();
        product.setStock(stock);
        productRepository.save(product);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        concurrentTest(threadCount, quantityPerRequest, successCount, failureCount);

        Product updated = productRepository.findById(productId).orElseThrow();

        assertThat(updated.getStock()).isEqualTo(1); // 5 - (2*2) = 1
        assertThat(successCount.get()).isEqualTo(2);
        assertThat(failureCount.get()).isEqualTo(8);
    }

    @Test
    @DisplayName("락 충돌 테스트 - 재고 1, 요청 10")
    void lockConflictTest() throws InterruptedException {
        // 단일 재고에 대한 락 경쟁 테스트: 1개의 재고에 10개 스레드가 접근, 1개만 성공해야 함
        int stock = 1;
        int threadCount = 10;
        int quantityPerRequest = 1;

        Product product = productRepository.findById(productId).orElseThrow();
        product.setStock(stock);
        productRepository.save(product);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        concurrentTest(threadCount, quantityPerRequest, successCount, failureCount);

        Product updated = productRepository.findById(productId).orElseThrow();
        assertThat(updated.getStock()).isEqualTo(0);
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failureCount.get()).isEqualTo(9);
    }

    @Test
    @DisplayName("동일 상품 예약 5건 동시 취소 - 수량 2 * 예약건 5 = 재고 10")
    void cancelMultipleReservationsTest() throws InterruptedException {
        int threadCount = 5;
        int quantityPerReservation = 2;
        int initialStock = 10;

        // 초기 재고 설정
        Product product = productRepository.findById(productId).orElseThrow();
        product.setStock(initialStock);
        productRepository.saveAndFlush(product);

        // 예약 여러 건 생성
        Long[] reservationIds = new Long[threadCount];
        Long[] userIds = new Long[threadCount];
        for (int i = 0; i < threadCount; i++) {
            User user = new User("test"+i+"@test.com",
                    "홍길동",
                    "01011112222",
                    "경기도 수원시",
                    BigDecimal.valueOf(37.123456),
                    BigDecimal.valueOf(126.123456));
            user.changePassword("test");
            userRepository.saveAndFlush(user);
            userIds[i] = user.getId();

            Reservation reservation = new Reservation(user, product, quantityPerReservation);
            reservation = reservationRepository.saveAndFlush(reservation);

            product.decreaseStock(quantityPerReservation);
            reservationIds[i] = reservation.getId();
        }
        productRepository.saveAndFlush(product);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            long reservationId = reservationIds[i];
            long userId = userIds[i];
            executorService.submit(() -> {
                try {
                    reservationService.cancelReservationByUser(reservationId, userId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.err.println("Cancel reservation failed: " + e.getMessage());
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        Product updatedProduct = productRepository.findById(productId).orElseThrow();

        assertThat(updatedProduct.getStock()).isEqualTo(initialStock);
        assertThat(successCount.get()).isEqualTo(threadCount);
        assertThat(failureCount.get()).isEqualTo(0);
    }

    /**
     * 동시성 테스트
     *
     * @param threadCount           실행할 스레드의 수
     * @param quantityPerRequest    요청 당 상품의 수량
     * @param successCount          요청 성공 횟수
     * @param failureCount          요청 실패 횟수
     */
    private void concurrentTest(int threadCount, int quantityPerRequest, AtomicInteger successCount, AtomicInteger failureCount) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    reservationService.reserveProduct(new ReservationRequest(productId, userId, quantityPerRequest));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.err.println("Reservation failed: " + e.getMessage());
                    failureCount.incrementAndGet();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        executorService.shutdown();
    }
}
