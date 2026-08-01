package com.boulderingnavigation.dto;

import java.util.List;

public record RockResponse(
        Long rockId,
        String name,
        List<ProblemSummaryResponse> problems
) {
}
