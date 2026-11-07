package dev.midnightcoder.midnightdashboard.dashboard;

import dev.midnightcoder.midnightdashboard.developer.internal.DeveloperRepository;
import dev.midnightcoder.midnightdashboard.developer.internal.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DashboardServiceTest {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private DeveloperRepository developerRepository;

    @BeforeEach
    void setUp() {
        for (Developer dev : developerRepository.findAll()) {
            dev.setMentor(null);
            developerRepository.save(dev);
        }
        developerRepository.deleteAll();

        Developer dev1 = new Developer(
            "Avery", "Stone", "avery@example.test", "avery-stone",
            DeveloperLevel.PRINCIPAL, DeveloperTeam.PLATFORM, DeveloperStatus.ACTIVE,
            90, new BigDecimal("10.0"), LocalDate.of(2020, 1, 1),
            OffsetDateTime.now(), null, "Architect and mentor"
        );
        developerRepository.save(dev1);

        Developer dev2 = new Developer(
                "Samira", "Okafor", "samira@example.test", "samira-okafor",
                DeveloperLevel.SENIOR, DeveloperTeam.BACKEND, DeveloperStatus.ON_LEAVE,
                80, new BigDecimal("7.0"), LocalDate.of(2021, 5, 10),
                OffsetDateTime.now(), dev1, "Senior engineer"
        );
        developerRepository.save(dev2);
    }

    @Test
    void testGetSummary() {
        DashboardSummary summary = dashboardService.getSummary();
        assertThat(summary.totalDevelopers()).isEqualTo(2);
        assertThat(summary.activeDevelopers()).isEqualTo(1);
        assertThat(summary.onLeaveDevelopers()).isEqualTo(1);
        assertThat(summary.inactiveDevelopers()).isEqualTo(0);
        assertThat(summary.averageProgress()).isEqualTo(85.0);
        assertThat(summary.levelCounts()).containsKeys(DeveloperLevel.PRINCIPAL, DeveloperLevel.SENIOR);
        assertThat(summary.teamCounts()).containsKeys(DeveloperTeam.PLATFORM, DeveloperTeam.BACKEND);
    }
}
