package com.java.luckyhankki.service;

import com.java.luckyhankki.config.security.CustomUserDetails;
import com.java.luckyhankki.domain.seller.Seller;
import com.java.luckyhankki.domain.seller.SellerRepository;
import com.java.luckyhankki.domain.user.User;
import com.java.luckyhankki.domain.user.UserRepository;
import com.java.luckyhankki.exception.ErrorCode;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UnifiedUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;

    public UnifiedUserDetailsService(UserRepository userRepository, SellerRepository sellerRepository) {
        this.userRepository = userRepository;
        this.sellerRepository = sellerRepository;
    }

    /**
     * username이 이메일('@')일 경우 userRepository에서 사용자 조회
     * 아닐 경우 sellerRepository에서 판매자 조회
     *
     * @param username 유저 네임(이메일 or 사업자등록번호)
     * @return CustomUserDetails
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.contains("@")) {
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.UNAUTHORIZED_USER.getMessage()));
            Set<SimpleGrantedAuthority> grantedAuthority = Set.of(new SimpleGrantedAuthority("ROLE_" + user.getRoleType().name()));

            return new CustomUserDetails(user.getId(), user.getEmail(), user.getPassword(), grantedAuthority);
        } else {
            Seller seller = sellerRepository.findByBusinessNumber(username)
                    .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.UNAUTHORIZED_USER.getMessage()));

            return new CustomUserDetails(seller.getId(), seller.getBusinessNumber(), seller.getPassword(), seller.getGrantedAuthority());
        }
    }
}
