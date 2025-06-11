package com.java.luckyhankki.controller;

import com.java.luckyhankki.dto.UserRequest;
import com.java.luckyhankki.dto.UserRegisterResponse;
import com.java.luckyhankki.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserRegisterResponse> registerUser(@Valid @RequestBody UserRequest request) {
        UserRegisterResponse user = userService.registerUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
