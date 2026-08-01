package com.boulderingnavigation.dto;

public record VideoResponse(
        Long videoId,
        String url,
        String platform
) {
}
