package dev.midnightcoder.midnightdashboard.developer.dto;

import dev.midnightcoder.midnightdashboard.developer.internal.DeveloperTeam;

public record DeveloperTeamCount(
    DeveloperTeam team,
    long count
) {}
