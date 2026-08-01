package com.boulderingnavigation.dto;

public record ProblemSummaryResponse(
        Long problemId,
        String name,
        String grade
) {
}
