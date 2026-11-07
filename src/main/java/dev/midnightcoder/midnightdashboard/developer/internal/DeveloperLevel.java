package dev.midnightcoder.midnightdashboard.developer.internal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeveloperLevel {
    FRESHER("Fresher"),
    INTERN("Intern"),
    JUNIOR("Junior"),
    MID_LEVEL("Mid-level"),
    SENIOR("Senior"),
    STAFF("Staff"),
    PRINCIPAL("Principal");

    private final String label;
}
