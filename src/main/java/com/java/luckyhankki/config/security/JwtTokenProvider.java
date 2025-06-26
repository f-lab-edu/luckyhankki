package com.java.luckyhankki.config.security;

import com.java.luckyhankki.service.UnifiedUserDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final SecretKey secretKey;
    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(@Value("${spring.jwt.secret}") String secret, UnifiedUserDetailsService userDetailsService) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.userDetailsService = userDetailsService;
    }

    /**
     * 유저 타입과 username, 역할 목록을 기반으로 JWT 토큰을 생성한다.
     *
     * @param username  유저 네임 (ex: 이메일 or 사업자등록번호)
     * @param role      유저 역할 목록
     * @return 생성된 JWT 토큰 문자열
     */
    public String createToken(String username, Collection<? extends GrantedAuthority> role) {
        LOGGER.info("[createToken] JWT 토큰 생성: username {} and role {}", username, role);

        long tokenValidMillisecond = 1000L * 60 * 60; //1시간
        Date now = new Date();
        Date expiry = new Date(now.getTime() + tokenValidMillisecond);

        Claims claims = Jwts.claims()
                .subject(username)
                .add("role", role)
                .build();

        String token = Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();

        LOGGER.info("[createToken] Created token: {}", token);
        return token;
    }

    /**
     * 필터에서 인증이 성공했을 때 SecurityContextHolder에 저장할 Authentication 객체를 생성한다.
     *
     * @param token JWT 토큰
     * @return Authentication 인증 객체
     */
    public Authentication getAuthentication(String token) {
        LOGGER.info("[getAuthentication] 토큰 인증 정보 조회 시작");

        String username = getUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        LOGGER.info("[getAuthentication] 토큰 인증 정보 조회 완료, userName: {}", userDetails.getUsername());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * JWT 토큰에서 Subject를 추출한다.
     *
     * @param token JWT 토큰
     * @return 토큰의 Subject를 리턴
     */
    public String getUsername(String token) {
        LOGGER.info("[getUsername] 토큰 기반 회원 구별 정보 추출");
        String info = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        LOGGER.info("[getUserName] 토큰 기반 회원 구별 정보 추출 완료, info: {}", info);
        return info;
    }

    /**
     * HTTP Request에서 JWT 토큰을 추출한다.
     *
     * @param request HTTP 요청
     * @return JWT 토큰 문자열 또는 null
     */
    public String resolveToken(HttpServletRequest request) {
        LOGGER.info("[resolveToken] HTTP 헤더에서 Token 값 추출");
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); //"Bearer " 제거
        }
        return null;
    }

    /**
     * JWT 토큰이 유효한지 확인한다.
     *
     * @param token JWT 토큰
     * @return 유효한 경우 true, 유효하지 않은 경우 false
     */
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            boolean isValid = !claims.getPayload().getExpiration().before(new Date());
            if (!isValid) {
                LOGGER.warn("Expired JWT token for subject: {}", claims.getPayload().getSubject());
            }
            return isValid;
        } catch (ExpiredJwtException e) {
            LOGGER.warn("Expired JWT token", e);
            return false;
        } catch (MalformedJwtException e) {
            LOGGER.warn("Malformed JWT token", e);
            return false;
        } catch (Exception e) {
            LOGGER.error("Invalid JWT token", e);
            return false;
        }
    }
}
