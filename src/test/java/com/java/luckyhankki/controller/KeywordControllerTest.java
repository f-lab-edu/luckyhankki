package com.java.luckyhankki.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.luckyhankki.dto.keyword.KeywordRequest;
import com.java.luckyhankki.dto.keyword.KeywordResponse;
import com.java.luckyhankki.service.KeywordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KeywordController.class)
@WithMockUser(username = "test", roles = "ADMIN")
class KeywordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KeywordService keywordService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("키워드 등록 웹 테스트")
    void addKeyword() throws Exception {
        String keyword = "음식이 맛있어요";
        KeywordRequest request = new KeywordRequest(keyword);
        KeywordResponse response = new KeywordResponse(1L, keyword);

        given(keywordService.addKeyword(request))
                .willReturn(response);

        mockMvc.perform(post("/keywords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.keyword").value(keyword));
    }

    @Test
    @DisplayName("키워드 목록 조회 웹 테스트")
    void getAllKeywords() throws Exception {
        List<KeywordResponse> responseList = List.of(
                new KeywordResponse(1L, "음식이 맛있어요"),
                new KeywordResponse(2L, "친절해요")
        );

        given(keywordService.getAllKeywords())
                .willReturn(responseList);

        mockMvc.perform(get("/keywords")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].keyword").value("음식이 맛있어요"))
                .andExpect(jsonPath("$[1].keyword").value("친절해요"))
                .andDo(print());
    }

    @Test
    @DisplayName("키워드 수정 웹 테스트")
    void updateKeyword() throws Exception {
        KeywordRequest request = new KeywordRequest("음식이 맛있어요");
        KeywordResponse response = new KeywordResponse(1L, "친절해요");

        given(keywordService.updateKeyword(eq(1L), any()))
                .willReturn(response);

        mockMvc.perform(put("/keywords/{keywordId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keyword").value("친절해요"));
    }

    @Test
    @DisplayName("키워드 삭제 웹 테스트")
    void deleteKeyword() throws Exception {
        doNothing().when(keywordService).deleteKeyword(1L);

        mockMvc.perform(delete("/keywords/{keywordId}", 1L)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}