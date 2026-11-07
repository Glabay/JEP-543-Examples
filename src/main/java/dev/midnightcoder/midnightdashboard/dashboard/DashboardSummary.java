package dev.midnightcoder.midnightdashboard.dashboard;

import dev.midnightcoder.midnightdashboard.developer.internal.DeveloperLevel;
import dev.midnightcoder.midnightdashboard.developer.internal.DeveloperTeam;

import java.util.Map;

public record DashboardSummary(
    long totalDevelopers,
    long activeDevelopers,
    long onLeaveDevelopers,
    long inactiveDevelopers,
    double averageProgress,
    Map<DeveloperLevel, Long> levelCounts,
    Map<DeveloperTeam, Long> teamCounts
) {}
