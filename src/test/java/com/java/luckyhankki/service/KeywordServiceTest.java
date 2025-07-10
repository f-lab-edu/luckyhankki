package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.keyword.Keyword;
import com.java.luckyhankki.domain.keyword.KeywordRepository;
import com.java.luckyhankki.dto.keyword.KeywordRequest;
import com.java.luckyhankki.dto.keyword.KeywordResponse;
import com.java.luckyhankki.exception.CustomException;
import com.java.luckyhankki.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeywordServiceTest {

    @Mock
    private KeywordRepository repository;

    @InjectMocks
    private KeywordService service;

    @Test
    @DisplayName("키워드 저장 성공")
    void addKeyword_success() {
        KeywordRequest request = new KeywordRequest("음식이 맛있어요");

        when(repository.save(any(Keyword.class)))
                .then(returnsFirstArg());

        KeywordResponse response = service.addKeyword(request);

        assertEquals("음식이 맛있어요", response.keyword());

        verify(repository).save(any(Keyword.class));
    }

    @Test
    @DisplayName("등록된 모든 키워드 조회 성공")
    void getAllKeywords_success() {
        List<Keyword> keywords = List.of(
                new Keyword("음식이 맛있어요"),
                new Keyword("친절해요")
        );

        when(repository.findAll()).thenReturn(keywords);

        List<KeywordResponse> result = service.getAllKeywords();

        assertThat(result).hasSize(2);
        assertThat(result).extracting("keyword")
                .containsExactlyInAnyOrder("음식이 맛있어요", "친절해요");
    }

    @Test
    @DisplayName("키워드 수정 성공")
    void updateKeyword_success() {
        Keyword keyword = new Keyword("음식이 맛있어요");
        when(repository.findByIdWithLock(any(Long.class)))
                .thenReturn(Optional.of(keyword));

        KeywordResponse response = service.updateKeyword(1L, new KeywordRequest("친절해요"));

        assertThat(response.keyword()).isEqualTo("친절해요");
    }

    @Test
    @DisplayName("키워드 수정-키워드 미존재 시 예외 발생")
    void updateKeyword_throwsException_whenKeywordNotFound() {
        when(repository.findByIdWithLock(any(Long.class)))
                .thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
                () -> service.updateKeyword(1L, new KeywordRequest("친절해요")));

        assertThat(exception.getMessage()).isEqualTo(ErrorCode.KEYWORD_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("키워드 삭제 성공")
    void deleteKeyword_success() {
        Keyword keyword = new Keyword("음식이 맛있어요");
        when(repository.findByIdWithLock(any(Long.class)))
                .thenReturn(Optional.of(keyword));

        service.deleteKeyword(1L);

        assertThat(keyword.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("키워드 삭제-키워드 미존재 시 예외 발생")
    void deleteKeyword_throwsException_whenKeywordNotFound() {
        when(repository.findByIdWithLock(any(Long.class)))
                .thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
                () -> service.deleteKeyword(1L));

        assertThat(exception.getMessage()).isEqualTo(ErrorCode.KEYWORD_NOT_FOUND.getMessage());
    }
}