package dev.midnightcoder.midnightdashboard.developer.internal;

import java.util.List;

public interface DeveloperRepositoryCustom {
    List<Developer> findAll(String level, String team, String status, String search);
}
