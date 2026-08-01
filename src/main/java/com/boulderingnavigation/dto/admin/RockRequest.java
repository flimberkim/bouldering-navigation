package com.boulderingnavigation.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RockRequest(
        @NotBlank String name,
        @NotNull Long mountainId
) {
}
