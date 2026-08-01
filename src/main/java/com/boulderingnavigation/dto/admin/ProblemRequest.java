package com.boulderingnavigation.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProblemRequest(
        @NotBlank String name,
        @NotBlank String grade,
        @NotNull Long rockId
) {
}
