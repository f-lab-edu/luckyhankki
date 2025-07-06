package com.java.luckyhankki.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.luckyhankki.dto.common.ErrorResponse;
import com.java.luckyhankki.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.error("[CustomAccessDeniedHandler] 권한 없음: {}, 요청 URI: {}", accessDeniedException, request.getRequestURI());
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.FORBIDDEN_USER);

        // HTTP 응답 설정
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // ObjectMapper로 JSON 직렬화
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
