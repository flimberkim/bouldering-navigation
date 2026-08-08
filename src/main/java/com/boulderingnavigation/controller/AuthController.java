package com.boulderingnavigation.controller;

import com.boulderingnavigation.dto.AuthResponse;
import com.boulderingnavigation.dto.LoginRequest;
import com.boulderingnavigation.dto.SignupRequest;
import com.boulderingnavigation.dto.UserResponse;
import com.boulderingnavigation.security.AuthUser;
import com.boulderingnavigation.service.AuthCommandService;
import com.boulderingnavigation.service.AuthQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthCommandService authCommandService;
    private final AuthQueryService authQueryService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody @Valid SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authCommandService.signup(request));
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest request) {
        return authCommandService.login(request);
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal AuthUser authUser) {
        return authQueryService.me(authUser.id());
    }
}
