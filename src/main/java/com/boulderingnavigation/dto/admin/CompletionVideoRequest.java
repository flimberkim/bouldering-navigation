package com.boulderingnavigation.dto.admin;

import com.boulderingnavigation.domain.VideoPlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CompletionVideoRequest(
        @NotBlank String url,
        @NotNull VideoPlatform platform,
        @NotNull Long problemId
) {
}
