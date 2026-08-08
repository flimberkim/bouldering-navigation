package com.boulderingnavigation.dto;

public record UserResponse(
        Long id,
        String email,
        String nickname
) {
}
