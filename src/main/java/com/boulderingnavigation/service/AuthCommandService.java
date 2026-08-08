package com.boulderingnavigation.service;

import com.boulderingnavigation.domain.User;
import com.boulderingnavigation.dto.AuthResponse;
import com.boulderingnavigation.dto.LoginRequest;
import com.boulderingnavigation.dto.SignupRequest;
import com.boulderingnavigation.exception.InvalidCredentialsException;
import com.boulderingnavigation.repository.UserRepository;
import com.boulderingnavigation.security.AuthUser;
import com.boulderingnavigation.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponse signup(SignupRequest request) {
        User user = userRepository.save(User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .build());
        userRepository.flush();
        return issueResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다."));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        return issueResponse(user);
    }

    private AuthResponse issueResponse(User user) {
        AuthUser authUser = new AuthUser(user.getId(), user.getEmail(), user.getNickname());
        String token = jwtTokenProvider.issue(authUser);
        return new AuthResponse(token, "Bearer", user.getId(), user.getNickname());
    }
}
