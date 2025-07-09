package com.java.luckyhankki.integration;

import com.java.luckyhankki.domain.keyword.Keyword;
import com.java.luckyhankki.domain.keyword.KeywordRepository;
import com.java.luckyhankki.dto.keyword.KeywordRequest;
import com.java.luckyhankki.service.KeywordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class KeywordIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(KeywordIntegrationTest.class);

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private KeywordService keywordService;

    private Keyword keyword;
    private Long keywordId;

    @BeforeEach
    void setUp() {
        keywordRepository.deleteAll();

        keyword = new Keyword("음식");
        keywordRepository.saveAndFlush(keyword);
        keywordId = keyword.getId();
    }

    @Test
    @DisplayName("키워드 수정 동시성 테스트")
    void concurrencyTest_updateKeyword() throws InterruptedException {
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        int threadNum = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        CountDownLatch latch = new CountDownLatch(threadNum);

        for (int i = 0; i < threadNum; i++) {
            String newKeyword = "식료품";
            executorService.execute(() -> {
                try {
                    keywordService.updateKeyword(keywordId, new KeywordRequest(newKeyword));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        assertThrows(ObjectOptimisticLockingFailureException.class, () -> keywordRepository.save(keyword));
    }

    @Test
    @DisplayName("키워드 삭제 동시성 테스트")
    void concurrencyTest_deleteKeyword() throws InterruptedException {
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        for (int i = 0; i < 2; i++) {
            executorService.execute(() -> {
                try {
                    keywordService.deleteKeyword(keywordId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        assertThrows(ObjectOptimisticLockingFailureException.class, () -> keywordRepository.save(keyword));
        assertEquals(1, successCount.get());
        assertEquals(1, failureCount.get());
    }
}
