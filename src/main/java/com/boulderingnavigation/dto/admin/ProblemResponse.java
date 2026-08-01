package com.boulderingnavigation.dto.admin;

public record ProblemResponse(
        Long id,
        String name,
        String grade,
        Long rockId,
        String rockName
) {
}
