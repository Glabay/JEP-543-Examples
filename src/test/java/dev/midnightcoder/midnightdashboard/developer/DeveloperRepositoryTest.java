package dev.midnightcoder.midnightdashboard.developer;

import dev.midnightcoder.midnightdashboard.developer.dto.CreateDeveloperRequest;
import dev.midnightcoder.midnightdashboard.developer.dto.ProgressUpdateRequest;
import dev.midnightcoder.midnightdashboard.developer.internal.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DeveloperRepositoryTest {

    @Autowired
    private DeveloperRepository developerRepository;

    private Developer mentor;
    private Developer seniorDev;
    private Developer juniorDev;

    @BeforeEach
    void setUp() {
        for (Developer dev : developerRepository.findAll()) {
            dev.setMentor(null);
            developerRepository.save(dev);
        }
        developerRepository.flush();
        developerRepository.deleteAll();
        developerRepository.flush();

        mentor = new Developer(
            "Avery", "Stone", "avery@example.test", "avery-stone",
            DeveloperLevel.PRINCIPAL, DeveloperTeam.PLATFORM, DeveloperStatus.ACTIVE,
            90, new BigDecimal("10.0"), LocalDate.of(2020, 1, 1),
            OffsetDateTime.now(), null, "Architect and mentor"
        );
        mentor = developerRepository.save(mentor);

        seniorDev = new Developer(
                "Samira", "Okafor", "samira@example.test", "samira-okafor",
                DeveloperLevel.SENIOR, DeveloperTeam.BACKEND, DeveloperStatus.ACTIVE,
                80, new BigDecimal("7.0"), LocalDate.of(2021, 5, 10),
                OffsetDateTime.now(), mentor, "Senior engineer"
        );
        seniorDev = developerRepository.save(seniorDev);

        juniorDev = new Developer(
                "Lucas", "Price", "lucas@example.test", "lucas-price",
                DeveloperLevel.JUNIOR, DeveloperTeam.BACKEND, DeveloperStatus.ON_LEAVE,
                50, new BigDecimal("2.0"), LocalDate.of(2024, 2, 1),
                OffsetDateTime.now(), mentor, "Junior engineer"
        );
        juniorDev = developerRepository.save(juniorDev);
    }

    @Test
    void testFindById() {
        var found = developerRepository.findById(seniorDev.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Samira");
        assertThat(found.get().getMentorId()).isEqualTo(mentor.getId());
        assertThat(found.get().getMentorName()).isEqualTo("Avery Stone");
        assertThat(found.get().getFullName()).isEqualTo("Samira Okafor");
        assertThat(found.get().getInitials()).isEqualTo("SO");
    }

    @Test
    void testFindAllWithFiltersAndOrdering() {
        // Find all developers (should order by level: PRINCIPAL (Avery), SENIOR (Samira), JUNIOR (Lucas))
        List<Developer> all = developerRepository.findAll(null, null, null, null);
        assertThat(all).hasSize(3);
        assertThat(all.get(0).getLevel()).isEqualTo(DeveloperLevel.PRINCIPAL);
        assertThat(all.get(1).getLevel()).isEqualTo(DeveloperLevel.SENIOR);
        assertThat(all.get(2).getLevel()).isEqualTo(DeveloperLevel.JUNIOR);

        // Filter by level
        List<Developer> seniors = developerRepository.findAll("SENIOR", null, null, null);
        assertThat(seniors).hasSize(1);
        assertThat(seniors.get(0).getLastName()).isEqualTo("Okafor");

        // Filter by team
        List<Developer> backend = developerRepository.findAll(null, "BACKEND", null, null);
        assertThat(backend).hasSize(2);

        // Filter by status
        List<Developer> onLeave = developerRepository.findAll(null, null, "ON_LEAVE", null);
        assertThat(onLeave).hasSize(1);
        assertThat(onLeave.get(0).getLastName()).isEqualTo("Price");

        // Search by name
        List<Developer> searched = developerRepository.findAll(null, null, null, "samira");
        assertThat(searched).hasSize(1);
        assertThat(searched.get(0).getFirstName()).isEqualTo("Samira");

        // Search by email
        List<Developer> searchEmail = developerRepository.findAll(null, null, null, "price");
        assertThat(searchEmail).hasSize(1);
        assertThat(searchEmail.get(0).getGithubUsername()).isEqualTo("lucas-price");
    }

    @Test
    void testCountMethods() {
        assertThat(developerRepository.countAll()).isEqualTo(3);
        assertThat(developerRepository.countActive()).isEqualTo(2);
        assertThat(developerRepository.countOnLeave()).isEqualTo(1);
        assertThat(developerRepository.countInactive()).isEqualTo(0);
        assertThat(developerRepository.averageProgress()).isCloseTo((90.0 + 80.0 + 50.0) / 3.0, org.assertj.core.data.Offset.offset(0.01));
    }

    @Test
    void testCountByLevel() {
        Map<DeveloperLevel, Long> levelCounts = developerRepository.countByLevel();
        assertThat(levelCounts).containsEntry(DeveloperLevel.PRINCIPAL, 1L);
        assertThat(levelCounts).containsEntry(DeveloperLevel.SENIOR, 1L);
        assertThat(levelCounts).containsEntry(DeveloperLevel.JUNIOR, 1L);
    }

    @Test
    void testCountByTeam() {
        Map<DeveloperTeam, Long> teamCounts = developerRepository.countByTeam();
        assertThat(teamCounts).containsEntry(DeveloperTeam.BACKEND, 2L);
        assertThat(teamCounts).containsEntry(DeveloperTeam.PLATFORM, 1L);
        // BACKEND has 2, PLATFORM has 1, so BACKEND must be first
        assertThat(teamCounts.keySet().iterator().next()).isEqualTo(DeveloperTeam.BACKEND);
    }

    @Test
    void testCreateAndUpdateProgress() {
        CreateDeveloperRequest request = new CreateDeveloperRequest(
                "Elena", "Rivera", "elena@example.test", "elena-rivera",
                DeveloperLevel.INTERN, DeveloperTeam.FRONTEND, DeveloperStatus.ACTIVE,
                40, new BigDecimal("0.5"), LocalDate.of(2025, 6, 1), mentor.getId(), "Frontend intern"
        );
        long newId = developerRepository.create(request);
        assertThat(newId).isPositive();

        var created = developerRepository.findById(newId);
        assertThat(created).isPresent();
        assertThat(created.get().getFullName()).isEqualTo("Elena Rivera");
        assertThat(created.get().getMentorId()).isEqualTo(mentor.getId());

        ProgressUpdateRequest updateReq = new ProgressUpdateRequest(DeveloperLevel.JUNIOR, 60);
        boolean updated = developerRepository.updateProgress(newId, updateReq);
        assertThat(updated).isTrue();

        var reloaded = developerRepository.findById(newId).orElseThrow();
        assertThat(reloaded.getLevel()).isEqualTo(DeveloperLevel.JUNIOR);
        assertThat(reloaded.getProgressPercent()).isEqualTo(60);
    }
}
