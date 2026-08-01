package com.boulderingnavigation.dto.admin;

public record RockResponse(
        Long id,
        String name,
        Long mountainId,
        String mountainName
) {
}
