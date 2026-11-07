package dev.midnightcoder.midnightdashboard.developer.internal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeveloperTeam {
    PLATFORM("Platform"),
    BACKEND("Backend"),
    FRONTEND("Frontend"),
    FULL_STACK("Full stack"),
    DEVOPS("DevOps"),
    MOBILE("Mobile"),
    DATA("Data"),
    QA("Quality engineering"),
    SECURITY("Security");

    private final String label;
}
