package com.java.luckyhankki.controller;

import com.java.luckyhankki.dto.keyword.KeywordRequest;
import com.java.luckyhankki.dto.keyword.KeywordResponse;
import com.java.luckyhankki.service.KeywordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Keyword", description = "키워드 관련 API")
@RestController
@RequestMapping("/keywords")
public class KeywordController {

    private final KeywordService keywordService;

    public KeywordController(KeywordService keywordService) {
        this.keywordService = keywordService;
    }

    @Operation(summary = "키워드 등록", description = "관리자가 키워드를 등록합니다.")
    @PostMapping
    public ResponseEntity<KeywordResponse> addKeyword(@Valid @RequestBody KeywordRequest request) {
        KeywordResponse keywordResponse = keywordService.addKeyword(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(keywordResponse);
    }

    @Operation(summary = "키워드 목록 조회", description = "등록된 키워드를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<KeywordResponse>> getAllKeywords() {
        List<KeywordResponse> keywords = keywordService.getAllKeywords();

        return ResponseEntity.status(HttpStatus.OK).body(keywords);
    }

    @Operation(summary = "키워드 수정", description = "관리자가 키워드 ID에 해당하는 키워드를 수정합니다.")
    @PutMapping("/{keywordId}")
    public ResponseEntity<KeywordResponse> updateKeyword(
            @Parameter(description = "키워드 ID") @PathVariable long keywordId,
            @Valid @RequestBody KeywordRequest request) {

        KeywordResponse keyword = keywordService.updateKeyword(keywordId, request);

        return ResponseEntity.status(HttpStatus.OK).body(keyword);
    }

    @Operation(summary = "키워드 삭제(soft delete)", description = "관리자가 키워드 ID에 해당하는 키워드를 삭제합니다.")
    @DeleteMapping("/{keywordId}")
    public ResponseEntity<Void> deleteKeyword(@Parameter(description = "키워드 ID") @PathVariable long keywordId) {
        keywordService.deleteKeyword(keywordId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
