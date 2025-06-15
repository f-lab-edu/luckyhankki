package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.user.User;
import com.java.luckyhankki.domain.user.UserRepository;
import com.java.luckyhankki.dto.UserRequest;
import com.java.luckyhankki.dto.UserRegisterResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원가입 성공")
    void registerUser_success() {
        UserRequest request = new UserRequest(
                "test@test.com",
                "abcAbc@123",
                "홍길동",
                "01011112222",
                "경기도 수원시",
                BigDecimal.valueOf(126.978414),
                BigDecimal.valueOf(37.566680)
        );

        User user = new User(
                request.email(),
                request.name(),
                request.phone(),
                request.address(),
                request.longitude(),
                request.latitude()
        );

        when(userRepository.existsByEmail(request.email()))
                .thenReturn(false);
        when(userRepository.save(any(User.class)))
                .then(returnsFirstArg());

        UserRegisterResponse response = userService.registerUser(request);

        assertEquals("홍길동", response.name());
        assertEquals("test@test.com", response.email());

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("이미 존재하는 이메일인 경우 예외 발생")
    void registerUser_throwsException_whenEmailAlreadyExists() {
        UserRequest request = new UserRequest(
                "test@test.com",
                "abcAbc@123",
                "홍길동",
                "01011112222",
                "경기도 수원시",
                BigDecimal.valueOf(126.978414),
                BigDecimal.valueOf(37.566680)
        );

        when(userRepository.existsByEmail(request.email()))
                .thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.registerUser(request));

        assertEquals("이미 존재하는 이메일입니다.", exception.getMessage());
        verify(userRepository).existsByEmail(request.email());
    }
}