package com.java.luckyhankki.service;

import com.java.luckyhankki.config.security.JwtTokenProvider;
import com.java.luckyhankki.dto.common.LoginResult;
import com.java.luckyhankki.dto.seller.SellerLoginRequest;
import com.java.luckyhankki.dto.user.UserLoginRequest;
import com.java.luckyhankki.exception.CustomException;
import com.java.luckyhankki.exception.ErrorCode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 인증 서비스
 */
@Service
public class AuthService {

    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserDetailsService userDetailsService,
                       JwtTokenProvider jwtTokenProvider,
                       PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 사용자, 관리자 로그인 처리 및 토큰 발급
     *
     * @param request 로그인 요청 정보
     * @return 발급된 JWT 정보
     */
    @Transactional(readOnly = true)
    public LoginResult userLogin(UserLoginRequest request) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        if (!passwordEncoder.matches(request.password(), userDetails.getPassword())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        return getToken(userDetails);
    }

    /**
     * 판매자 로그인 처리 및 토큰 발급
     *
     * @param request 로그인 요청 정보
     * @return 발급된 JWT 정보
     */
    @Transactional(readOnly = true)
    public LoginResult sellerLogin(SellerLoginRequest request) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.businessNumber());
        if (!passwordEncoder.matches(request.password(), userDetails.getPassword())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        return getToken(userDetails);
    }

    /**
     * UserDetails의 정보를 가지고 토큰 발급
     *
     * @param userDetails
     * @return 로그인 응답 DTO 및 토큰 정보
     */
    private LoginResult getToken(UserDetails userDetails) {
        String token = jwtTokenProvider.createToken(userDetails.getUsername(), userDetails.getAuthorities());
        return new LoginResult("success", "로그인 성공", token);
    }
}
