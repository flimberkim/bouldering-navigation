package com.boulderingnavigation.dto;

public record AuthResponse(
        String token,
        String tokenType,
        Long userId,
        String nickname
) {
}
