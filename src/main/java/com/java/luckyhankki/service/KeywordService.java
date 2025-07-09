package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.keyword.Keyword;
import com.java.luckyhankki.domain.keyword.KeywordRepository;
import com.java.luckyhankki.dto.keyword.KeywordRequest;
import com.java.luckyhankki.dto.keyword.KeywordResponse;
import com.java.luckyhankki.exception.CustomException;
import com.java.luckyhankki.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KeywordService {

    private static final Logger log = LoggerFactory.getLogger(KeywordService.class);

    private final KeywordRepository keywordRepository;

    public KeywordService(KeywordRepository keywordRepository) {
        this.keywordRepository = keywordRepository;
    }

    @Transactional
    public KeywordResponse addKeyword(KeywordRequest request) {
        String keyword = request.keyword();
        if (keywordRepository.existsByKeyword(keyword)) {
            throw new CustomException(ErrorCode.KEYWORD_ALREADY_EXISTS);
        }

        return getResponse(keywordRepository.save(new Keyword(keyword)));
    }

    @Transactional(readOnly = true)
    public List<KeywordResponse> getAllKeywords() {
        return keywordRepository.findAll().stream()
                .map(KeywordService::getResponse)
                .toList();
    }

    /**
     * 키워드 수정 메소드
     * - 낙관적 락 사용
     * - ObjectOptimisticLockingFailureException 발생 시 @Retryable로 최대 3번 재시도됨
     * - 재시도 실패 시 recoverUpdateKeyword()에서 CustomException 반환
     *
     * @param keywordId 키워드 ID
     * @param request   키워드 수정 요청 DTO
     * @return  수정된 키워드
     */
    @Retryable(retryFor = ObjectOptimisticLockingFailureException.class)
    @Transactional
    public KeywordResponse updateKeyword(long keywordId, KeywordRequest request) {
        Keyword keyword = keywordRepository.findByIdWithLock(keywordId)
                .orElseThrow(() -> new CustomException(ErrorCode.KEYWORD_NOT_FOUND));
        keyword.changeKeyword(request.keyword());

        return getResponse(keyword);
    }

    /**
     * 키워드 삭제 메소드 (Soft Delete)
     * - 낙관적 락 사용
     * - boolean 필드 값 변경이므로, isDeleted가 true가 된 상태라면 재시도 없이 CustomException 반환
     *
     * @param keywordId 키워드 ID
     * @throws CustomException KEYWORD_ALREADY_DELETED
     */
    @Transactional
    public void deleteKeyword(long keywordId) {
        Keyword keyword = keywordRepository.findByIdWithLock(keywordId)
                .orElseThrow(() -> new CustomException(ErrorCode.KEYWORD_NOT_FOUND));
        try {
            keyword.deleteKeyword();
            keywordRepository.saveAndFlush(keyword);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new CustomException(ErrorCode.KEYWORD_ALREADY_DELETED);
        }
    }

    /**
     * updateKeyword 메소드에서 낙관적 락 재시도 실패 시 호출되는 fallback 메소드
     *
     * @param keywordId 키워드 ID
     * @param request   키워드 수정 요청 DTO
     * @throws CustomException KEYWORD_UPDATE_CONFLICT
     */
    @Recover
    private KeywordResponse recoverUpdateKeyword(ObjectOptimisticLockingFailureException e, long keywordId, KeywordRequest request) {
        log.error("[recoverUpdateKeyword] 재시도 실패: keywordId={}, keyword={}", keywordId, request.keyword());
        throw new CustomException(ErrorCode.KEYWORD_UPDATE_CONFLICT);
    }

    private static KeywordResponse getResponse(Keyword keyword) {
        return new KeywordResponse(keyword.getId(), keyword.getKeyword());
    }
}
