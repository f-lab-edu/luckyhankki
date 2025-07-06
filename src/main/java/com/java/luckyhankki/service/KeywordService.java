package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.keyword.Keyword;
import com.java.luckyhankki.domain.keyword.KeywordRepository;
import com.java.luckyhankki.dto.keyword.KeywordRequest;
import com.java.luckyhankki.dto.keyword.KeywordResponse;
import com.java.luckyhankki.exception.CustomException;
import com.java.luckyhankki.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KeywordService {

    private final KeywordRepository keywordRepository;

    public KeywordService(KeywordRepository keywordRepository) {
        this.keywordRepository = keywordRepository;
    }

    @Transactional
    public KeywordResponse addKeyword(KeywordRequest request) {
        Keyword savedKeyword = keywordRepository.save(new Keyword(request.keyword()));

        return getResponse(savedKeyword);
    }

    @Transactional(readOnly = true)
    public List<KeywordResponse> getAllKeywords() {
        return keywordRepository.findAll().stream()
                .map(KeywordService::getResponse)
                .toList();
    }

    @Transactional
    public KeywordResponse updateKeyword(long keywordId, KeywordRequest request) {
        Keyword keyword = keywordRepository.findById(keywordId)
                .orElseThrow(() -> new CustomException(ErrorCode.KEYWORD_NOT_FOUND));
        keyword.changeKeyword(request.keyword());

        return getResponse(keyword);
    }

    @Transactional
    public void deleteKeyword(long keywordId) {
        Keyword keyword = keywordRepository.findById(keywordId)
                .orElseThrow(() -> new CustomException(ErrorCode.KEYWORD_NOT_FOUND));
        keyword.deleteKeyword();
    }

    private static KeywordResponse getResponse(Keyword keyword) {
        return new KeywordResponse(keyword.getId(), keyword.getKeyword());
    }
}
