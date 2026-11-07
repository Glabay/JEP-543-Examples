package dev.midnightcoder.midnightdashboard.api;

import dev.midnightcoder.midnightdashboard.developer.internal.DeveloperRepository;
import dev.midnightcoder.midnightdashboard.developer.internal.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
class PageControllerTest {

    @Autowired
    private PageController pageController;

    @Autowired
    private DeveloperRepository developerRepository;

    private MockMvc mockMvc;
    private Developer developer;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pageController).build();
        for (Developer dev : developerRepository.findAll()) {
            dev.setMentor(null);
            developerRepository.save(dev);
        }
        developerRepository.deleteAll();

        developer = new Developer(
            "Avery", "Stone", "avery@example.test", "avery-stone",
            DeveloperLevel.PRINCIPAL, DeveloperTeam.PLATFORM, DeveloperStatus.ACTIVE,
            90, new BigDecimal("10.0"), LocalDate.of(2020, 1, 1),
            OffsetDateTime.now(), null, "Architect and mentor"
        );
        developer = developerRepository.save(developer);
    }

    @Test
    void testDashboardPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("summary", "developers", "levels", "teams", "statuses"));
    }

    @Test
    void testDeveloperDetailPage() throws Exception {
        mockMvc.perform(get("/developers/" + developer.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("developer-detail"))
                .andExpect(model().attributeExists("developer"));
    }
}
