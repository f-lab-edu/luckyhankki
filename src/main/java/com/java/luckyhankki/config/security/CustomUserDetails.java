package com.java.luckyhankki.config.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

public class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final String username;
    private final String password;
    private final Set<SimpleGrantedAuthority> grantedAuthority;

    public CustomUserDetails(Long userId, String username, String password, Set<SimpleGrantedAuthority> grantedAuthority) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.grantedAuthority = grantedAuthority;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthority;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
