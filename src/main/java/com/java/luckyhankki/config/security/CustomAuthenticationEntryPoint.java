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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("[CustomAuthenticationEntryPoint] 미인증: {}", authException.getMessage());
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.UNAUTHORIZED_USER);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
