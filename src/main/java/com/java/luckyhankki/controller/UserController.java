package com.java.luckyhankki.controller;

import com.java.luckyhankki.dto.common.LoginResult;
import com.java.luckyhankki.dto.user.UserLoginRequest;
import com.java.luckyhankki.dto.user.UserRegisterResponse;
import com.java.luckyhankki.dto.user.UserRequest;
import com.java.luckyhankki.service.AuthService;
import com.java.luckyhankki.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "유저 관련 API")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @Operation(summary = "회원 가입", description = "회원 가입을 진행합니다.")
    @PostMapping
    public ResponseEntity<UserRegisterResponse> registerUser(@Valid @RequestBody UserRequest request) {
        UserRegisterResponse user = userService.registerUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Operation(summary = "로그인", description = "로그인을 진행합니다.")
    @PostMapping("/login")
    public ResponseEntity<LoginResult> login(@Valid @RequestBody UserLoginRequest request) {
        LoginResult result = authService.userLogin(request);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
