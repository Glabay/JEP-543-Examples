package dev.midnightcoder.midnightdashboard.developer.dto;

import dev.midnightcoder.midnightdashboard.developer.internal.DeveloperLevel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ProgressUpdateRequest(
    @NotNull
    DeveloperLevel level,
    @Min(0)
    @Max(100)
    int progressPercent
) {}
