package com.boulderingnavigation.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MountainRequest(
        @NotBlank String name,
        @NotNull Long regionId
) {
}
