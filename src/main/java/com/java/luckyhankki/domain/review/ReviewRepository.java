package com.java.luckyhankki.domain.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = {"product", "keywords"})
    Page<Review> findAllByUserId(Long userId, Pageable pageable);

    void deleteByIdAndUserId(Long id, Long userId);

    boolean existsReviewByUserIdAndProductId(Long userId, Long productId);
}
