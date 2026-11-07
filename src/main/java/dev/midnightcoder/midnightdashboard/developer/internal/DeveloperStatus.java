package dev.midnightcoder.midnightdashboard.developer.internal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeveloperStatus {
    ACTIVE("Active"),
    ON_LEAVE("On leave"),
    INACTIVE("Inactive");

    private final String label;
}
