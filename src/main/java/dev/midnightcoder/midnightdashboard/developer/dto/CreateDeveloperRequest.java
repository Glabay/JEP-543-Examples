package dev.midnightcoder.midnightdashboard.developer.dto;

import dev.midnightcoder.midnightdashboard.developer.internal.DeveloperLevel;
import dev.midnightcoder.midnightdashboard.developer.internal.DeveloperStatus;
import dev.midnightcoder.midnightdashboard.developer.internal.DeveloperTeam;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateDeveloperRequest(
    @NotBlank
    @Size(max = 80)
    String firstName,
    @NotBlank
    @Size(max = 80)
    String lastName,
    @NotBlank
    @Email
    @Size(max = 180)
    String email,
    @NotBlank
    @Pattern(regexp = "[A-Za-z0-9-]{1,39}")
    String githubUsername,
    @NotNull
    DeveloperLevel level,
    @NotNull
    DeveloperTeam team,
    @NotNull
    DeveloperStatus status,
    @Min(0)
    @Max(100)
    int progressPercent,
    @NotNull
    @DecimalMin("0.0")
    BigDecimal experienceYears,
    @NotNull
    LocalDate joinedOn,
    Long mentorId,
    @Size(max = 1000)
    String bio
) {}
