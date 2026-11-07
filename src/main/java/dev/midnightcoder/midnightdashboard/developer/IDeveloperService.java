package dev.midnightcoder.midnightdashboard.developer;

import dev.midnightcoder.midnightdashboard.developer.dto.CreateDeveloperRequest;
import dev.midnightcoder.midnightdashboard.developer.dto.ProgressUpdateRequest;
import dev.midnightcoder.midnightdashboard.developer.internal.Developer;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

public interface IDeveloperService {
    List<Developer> findDevelopers(String level, String team, String status, String search);

    Developer findDeveloper(long id);

    @Transactional
    Developer createDeveloper(CreateDeveloperRequest request);

    @Transactional
    Developer updateProgress(long id, ProgressUpdateRequest request);

    default String normalize(String value) {
        return value == null || value.isBlank()
                ? null
                : value.trim().toUpperCase(Locale.ROOT);
    }
}
