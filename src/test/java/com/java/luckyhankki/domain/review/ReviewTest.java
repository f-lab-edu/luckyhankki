package com.java.luckyhankki.domain.review;

import com.java.luckyhankki.domain.category.Category;
import com.java.luckyhankki.domain.category.CategoryRepository;
import com.java.luckyhankki.domain.keyword.Keyword;
import com.java.luckyhankki.domain.keyword.KeywordRepository;
import com.java.luckyhankki.domain.product.Product;
import com.java.luckyhankki.domain.product.ProductRepository;
import com.java.luckyhankki.domain.seller.Seller;
import com.java.luckyhankki.domain.seller.SellerRepository;
import com.java.luckyhankki.domain.store.Store;
import com.java.luckyhankki.domain.store.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class ReviewTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private StoreRepository storeRepository;

    private Product product;
    private List<Keyword> keywords;

    @BeforeEach
    void setUp() {
        // Keyword 저장
        Keyword keyword1 = new Keyword("음식이 맛있어요");
        Keyword keyword2 = new Keyword("친절해요");
        keywords = keywordRepository.saveAll(List.of(keyword1, keyword2));

        // Seller 저장
        Seller seller = new Seller("1234567890", "판매자1", "abc!@#5", "abc@test.com");
        sellerRepository.save(seller);

        // Store 저장
        Store store = new Store(
                seller,
                "원조비빔밥가게",
                "031-123-4567",
                "경기도 수원시",
                BigDecimal.valueOf(126.978414),
                BigDecimal.valueOf(37.566680)
        );
        storeRepository.save(store);

        // Category 저장
        Category category = new Category("음식");
        categoryRepository.save(category);

        // 상품 저장
        product = new Product(
                store,
                category,
                "비빔밥",
                10000,
                8000,
                3,
                "육회비빔밥 입니다.",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );
        productRepository.save(product);
    }

    @Test
    @DisplayName("리뷰 저장 및 조회 테스트")
    void saveReview_andFindById() {
        Review request = new Review(1L, product, keywords, 5, "정말 맛있어요");
        reviewRepository.save(request);

        Review review = reviewRepository.findById(1L).orElseThrow(RuntimeException::new);
        assertEquals(review.getRatingScore(), request.getRatingScore());
        assertEquals(1L, review.getUserId());
    }

    @Test
    @DisplayName("리뷰 페이징 조회 테스트")
    void findAllByUserId() {
        long userId = 1L;
        reviewRepository.save(new Review(userId, product, keywords, 5, "정말 맛있어요"));
        reviewRepository.save(new Review(userId, product, keywords, 3, ""));
        reviewRepository.save(new Review(userId, product, keywords, 2, "별로에요"));

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Review> reviews = reviewRepository.findAllByUserId(userId, pageable);

        assertEquals(3, reviews.getTotalElements());
        assertEquals(2, reviews.getContent().get(0).getRatingScore()); //정렬 Desc
    }

    @Test
    @DisplayName("리뷰 삭제 테스트")
    void deleteByIdAndUserId() {
        long userId = 1L;
        Review review = new Review(userId, product, keywords, 5, "정말 맛있어요");
        reviewRepository.save(review);

        reviewRepository.deleteByIdAndUserId(review.getId(), userId);

        boolean isEmpty = reviewRepository.findById(review.getId()).isEmpty();
        assertTrue(isEmpty);
    }
}