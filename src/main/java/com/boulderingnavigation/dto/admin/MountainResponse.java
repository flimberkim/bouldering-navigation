package com.boulderingnavigation.dto.admin;

public record MountainResponse(
        Long id,
        String name,
        Long regionId,
        String regionName
) {
}
