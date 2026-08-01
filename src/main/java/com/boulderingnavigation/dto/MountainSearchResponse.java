package com.boulderingnavigation.dto;

import java.util.List;

public record MountainSearchResponse(
        Long mountainId,
        String name,
        String regionName,
        List<RockResponse> rocks
) {
}
