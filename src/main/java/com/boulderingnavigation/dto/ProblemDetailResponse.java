package com.boulderingnavigation.dto;

import java.util.List;

public record ProblemDetailResponse(
        Long problemId,
        String name,
        String grade,
        String rockName,
        String mountainName,
        String regionName,
        List<VideoResponse> videos
) {
}
