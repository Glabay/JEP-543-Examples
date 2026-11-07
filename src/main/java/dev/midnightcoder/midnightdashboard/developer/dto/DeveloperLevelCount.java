package dev.midnightcoder.midnightdashboard.developer.dto;

import dev.midnightcoder.midnightdashboard.developer.internal.DeveloperLevel;

public record DeveloperLevelCount(
    DeveloperLevel level,
    long count
) {}
