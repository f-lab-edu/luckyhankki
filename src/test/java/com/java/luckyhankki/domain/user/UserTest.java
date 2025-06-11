package com.java.luckyhankki.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("회원 가입 성공")
    void save_and_findById() {
        User user = new User(
                "test@test.com",
                "홍길동",
                "01011112222",
                "경기도 수원시",
                BigDecimal.valueOf(126.978414),
                BigDecimal.valueOf(37.566680)
        );
        user.changePassword("test");

        User savedUser = userRepository.save(user);
        Optional<User> optionalUser = userRepository.findById(savedUser.getId());

        assertThat(optionalUser).isPresent();
    }
}