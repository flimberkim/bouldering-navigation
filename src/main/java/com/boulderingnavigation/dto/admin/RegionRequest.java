package com.boulderingnavigation.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record RegionRequest(
        @NotBlank String name
) {
}
