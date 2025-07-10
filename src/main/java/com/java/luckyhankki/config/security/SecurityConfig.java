package com.java.luckyhankki.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;

@Configuration
public class SecurityConfig {

    //permitAll GET URL
    private static final String[] WHITE_LIST_GET_URL = {
            "/products",
            "/products/**",
            "/categories/**",
            "/stores/*",
            "/keywords/*"
    };

    //permitAll POST URL
    private static final String[] WHITE_LIST_POST_URL = {
            "/sellers",
            "/sellers/login",
            "/users",
            "/users/login"
    };

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, ObjectMapper objectMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                //REST API에서는 브라우저 사용 환경이 아니므로 CSRF 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                //JWT 토큰으로 인증을 처리하며, 세션은 사용하지 않기 때문에 STATELESS로 설정
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                //UI를 사용하는 것을 기본값으로 가진 시큐리티를 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)

                //애플리케이션에 들어오는 요청에 대한 사용 권한 체크
                //먼저 매칭되는 규칙이 적용(더 구체적인 경로 → 더 포괄적인 경로)
                .authorizeHttpRequests(authorize ->
                        authorize
                                //Swagger 관련 URL
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                                .requestMatchers(POST, "/categories", "/stores", "/keywords").hasRole("ADMIN")
                                .requestMatchers(PUT, "/categories/**", "/keywords/**").hasRole("ADMIN")
                                .requestMatchers(DELETE, "/keywords/**").hasRole("ADMIN")

                                .requestMatchers(GET, "/stores/*/products", "/reservations/stores/**").hasAnyRole("SELLER", "ADMIN")
                                .requestMatchers(POST, "/stores", "/products").hasRole("SELLER")
                                .requestMatchers(PUT, "/stores/**", "/products/**", "/reservations/**").hasRole("SELLER")
                                .requestMatchers(DELETE, "/stores/**", "/products/**").hasAnyRole("SELLER", "ADMIN")

                                .requestMatchers(GET, "/reservations/**").hasAnyRole("CUSTOMER", "ADMIN")
                                .requestMatchers(POST,"/reservations").hasRole("CUSTOMER")

                                .requestMatchers(GET, WHITE_LIST_GET_URL).permitAll()
                                .requestMatchers(POST, WHITE_LIST_POST_URL).permitAll()

                                .requestMatchers("/users/**").permitAll()
                                .anyRequest().authenticated())

                //로그인 폼 비활성화
                .formLogin(AbstractHttpConfigurer::disable)

                //생성한 JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter의 앞에 배치
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)

                //인증/인가 과정에서 예외가 발생하는 경우 사용할 핸들러 설정
                .exceptionHandling((exceptionHandling) ->
                        exceptionHandling
                                .authenticationEntryPoint(new CustomAuthenticationEntryPoint(objectMapper))
                                .accessDeniedHandler(new CustomAccessDeniedHandler(objectMapper)));

        return httpSecurity.build();
    }
}
