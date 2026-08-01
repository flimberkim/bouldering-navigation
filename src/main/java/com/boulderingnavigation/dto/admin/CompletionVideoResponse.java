package com.boulderingnavigation.dto.admin;

public record CompletionVideoResponse(
        Long id,
        String url,
        String platform,
        Long problemId
) {
}
