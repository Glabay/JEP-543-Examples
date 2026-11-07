package dev.midnightcoder.midnightdashboard.developer;

import dev.midnightcoder.midnightdashboard.developer.dto.CreateDeveloperRequest;
import dev.midnightcoder.midnightdashboard.developer.dto.ProgressUpdateRequest;
import dev.midnightcoder.midnightdashboard.developer.internal.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class DeveloperServiceTest {

    @Autowired
    private DeveloperService developerService;

    @Autowired
    private DeveloperRepository developerRepository;

    private Developer mentor;

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
    }

    @Test
    void testCreateDeveloper() {
        CreateDeveloperRequest request = new CreateDeveloperRequest(
                "Samira", "Okafor", "samira@example.test", "samira-okafor",
                DeveloperLevel.SENIOR, DeveloperTeam.BACKEND, DeveloperStatus.ACTIVE,
                80, new BigDecimal("7.0"), LocalDate.of(2021, 5, 10),
                mentor.getId(), "Senior engineer"
        );

        Developer created = developerService.createDeveloper(request);
        assertThat(created.getId()).isNotNull();
        assertThat(created.getFullName()).isEqualTo("Samira Okafor");
        assertThat(created.getMentorId()).isEqualTo(mentor.getId());
        assertThat(created.getMentorName()).isEqualTo("Avery Stone");
    }

    @Test
    void testFindDeveloper() {
        Developer found = developerService.findDeveloper(mentor.getId());
        assertThat(found.getFullName()).isEqualTo("Avery Stone");

        assertThatThrownBy(() -> developerService.findDeveloper(99999L))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void testUpdateProgress() {
        ProgressUpdateRequest updateReq = new ProgressUpdateRequest(DeveloperLevel.STAFF, 95);
        Developer updated = developerService.updateProgress(mentor.getId(), updateReq);

        assertThat(updated.getLevel()).isEqualTo(DeveloperLevel.STAFF);
        assertThat(updated.getProgressPercent()).isEqualTo(95);

        assertThatThrownBy(() -> developerService.updateProgress(99999L, updateReq))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void testFindDevelopersWithNormalization() {
        var developers = developerService.findDevelopers("principal", "platform", "active", "stone");
        assertThat(developers).hasSize(1);
        assertThat(developers.get(0).getId()).isEqualTo(mentor.getId());
    }
}
