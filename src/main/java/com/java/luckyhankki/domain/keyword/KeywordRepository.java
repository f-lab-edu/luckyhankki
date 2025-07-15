package com.java.luckyhankki.domain.keyword;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    boolean existsByKeyword(String keyword);

    //낙관적 락을 사용한 키워드 조회
    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT k FROM Keyword k WHERE k.id = :id")
    Optional<Keyword> findByIdWithLock(Long id);
}
