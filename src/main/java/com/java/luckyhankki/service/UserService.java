package com.java.luckyhankki.service;

import com.java.luckyhankki.domain.user.User;
import com.java.luckyhankki.domain.user.UserRepository;
import com.java.luckyhankki.dto.user.UserRegisterResponse;
import com.java.luckyhankki.dto.user.UserRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserRegisterResponse registerUser(UserRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        User user = new User(
                request.email(),
                request.name(),
                request.phone(),
                request.address(),
                request.longitude(),
                request.latitude()
        );
        user.changePassword(passwordEncoder.encode(request.password()));

        try {
            User savedUser = repository.save(user);

            return new UserRegisterResponse(
                    savedUser.getId(),
                    savedUser.getName(),
                    savedUser.getEmail(),
                    savedUser.getRoleType().name(),
                    savedUser.getCreatedAt()
            );
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("이미 존재하는 이메일로 회원가입에 실패했습니다.");
        }
    }
}
