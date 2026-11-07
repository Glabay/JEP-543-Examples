package dev.midnightcoder.midnightdashboard.developer.internal;

import dev.midnightcoder.midnightdashboard.developer.dto.CreateDeveloperRequest;
import dev.midnightcoder.midnightdashboard.developer.dto.DeveloperLevelCount;
import dev.midnightcoder.midnightdashboard.developer.dto.DeveloperTeamCount;
import dev.midnightcoder.midnightdashboard.developer.dto.ProgressUpdateRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public interface DeveloperRepository extends JpaRepository<Developer, Long>, DeveloperRepositoryCustom {

    @Override
    @EntityGraph(attributePaths = {"mentor"})
    Optional<Developer> findById(Long id);

    default long countAll() {
        return count();
    }

    long countByStatus(DeveloperStatus status);

    default long countActive() {
        return countByStatus(DeveloperStatus.ACTIVE);
    }

    default long countOnLeave() {
        return countByStatus(DeveloperStatus.ON_LEAVE);
    }

    default long countInactive() {
        return countByStatus(DeveloperStatus.INACTIVE);
    }

    @Query("SELECT COALESCE(AVG(d.progressPercent), 0.0) FROM Developer d")
    double averageProgress();

    @Query("SELECT new dev.midnightcoder.midnightdashboard.developer.dto.DeveloperLevelCount(d.level, COUNT(d)) FROM Developer d GROUP BY d.level")
    List<DeveloperLevelCount> countGroupedByLevel();

    default Map<DeveloperLevel, Long> countByLevel() {
        List<DeveloperLevel> order = List.of(
                DeveloperLevel.FRESHER,
                DeveloperLevel.INTERN,
                DeveloperLevel.JUNIOR,
                DeveloperLevel.MID_LEVEL,
                DeveloperLevel.SENIOR,
                DeveloperLevel.STAFF,
                DeveloperLevel.PRINCIPAL
        );
        Map<DeveloperLevel, Long> rawMap = countGroupedByLevel().stream()
                .collect(Collectors.toMap(DeveloperLevelCount::level, DeveloperLevelCount::count));
        Map<DeveloperLevel, Long> counts = new LinkedHashMap<>();
        for (DeveloperLevel level : order) {
            if (rawMap.containsKey(level)) {
                counts.put(level, rawMap.get(level));
            }
        }
        return counts;
    }

    @Query("SELECT new dev.midnightcoder.midnightdashboard.developer.dto.DeveloperTeamCount(d.team, COUNT(d)) FROM Developer d GROUP BY d.team")
    List<DeveloperTeamCount> countGroupedByTeam();

    default Map<DeveloperTeam, Long> countByTeam() {
        return countGroupedByTeam().stream()
                .sorted(Comparator.comparing(DeveloperTeamCount::count).reversed()
                        .thenComparing(dto -> dto.team().name()))
                .collect(Collectors.toMap(
                        DeveloperTeamCount::team,
                        DeveloperTeamCount::count,
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));
    }

    default long create(CreateDeveloperRequest request) {
        if (request == null) {
            return -1L;
        }
        Developer mentor = null;
        if (request.mentorId() != null) {
            mentor = findById(request.mentorId()).orElse(null);
        }
        var developer = Developer.builder()
            .firstName(request.firstName().trim())
            .lastName(request.lastName().trim())
            .email(request.email().trim().toLowerCase(Locale.ROOT))
            .githubUsername(request.githubUsername().trim())
            .level(request.level())
            .team(request.team())
            .status(request.status())
            .progressPercent(request.progressPercent())
            .experienceYears(request.experienceYears())
            .joinedOn(request.joinedOn())
            .mentor(mentor)
            .bio(request.bio())
            .build();
        return save(developer).getId();
    }

    default boolean updateProgress(long id, ProgressUpdateRequest request) {
        Optional<Developer> optional = findById(id);
        if (optional.isEmpty()) {
            return false;
        }
        Developer developer = optional.get();
        developer.setLevel(request.level());
        developer.setProgressPercent(request.progressPercent());
        developer.setLastActiveAt(OffsetDateTime.now());
        save(developer);
        return true;
    }
}
