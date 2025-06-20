package com.java.luckyhankki.controller;

import com.java.luckyhankki.dto.UserRequest;
import com.java.luckyhankki.dto.UserRegisterResponse;
import com.java.luckyhankki.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "유저 관련 API")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "회원 가입", description = "회원 가입을 진행합니다.")
    @PostMapping
    public ResponseEntity<UserRegisterResponse> registerUser(@Valid @RequestBody UserRequest request) {
        UserRegisterResponse user = userService.registerUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
