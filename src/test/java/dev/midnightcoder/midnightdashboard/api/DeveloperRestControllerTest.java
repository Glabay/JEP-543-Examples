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

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class DeveloperRestControllerTest {

    @Autowired
    private DeveloperRestController developerRestController;

    @Autowired
    private DeveloperRepository developerRepository;

    private MockMvc mockMvc;
    private Developer developer;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(developerRestController).build();
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
    void testFindDevelopers() throws Exception {
        mockMvc.perform(get("/api/v1/developers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Avery"))
                .andExpect(jsonPath("$[0].lastName").value("Stone"))
                .andExpect(jsonPath("$[0].fullName").value("Avery Stone"))
                .andExpect(jsonPath("$[0].initials").value("AS"));
    }

    @Test
    void testFindDeveloperById() throws Exception {
        mockMvc.perform(get("/api/v1/developers/" + developer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Avery"))
                .andExpect(jsonPath("$.email").value("avery@example.test"));
    }

    @Test
    void testCreateDeveloper() throws Exception {
        String json = """
                {
                    "firstName": "Jordan",
                    "lastName": "Miles",
                    "email": "jordan@example.test",
                    "githubUsername": "jordan-miles",
                    "level": "PRINCIPAL",
                    "team": "SECURITY",
                    "status": "ACTIVE",
                    "progressPercent": 88,
                    "experienceYears": 14.0,
                    "joinedOn": "2019-08-19",
                    "bio": "Security expert"
                }
                """;

        mockMvc.perform(post("/api/v1/developers")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Jordan"))
                .andExpect(jsonPath("$.lastName").value("Miles"))
                .andExpect(jsonPath("$.fullName").value("Jordan Miles"));
    }

    @Test
    void testUpdateProgress() throws Exception {
        String json = """
                {
                    "level": "STAFF",
                    "progressPercent": 95
                }
                """;

        mockMvc.perform(patch("/api/v1/developers/" + developer.getId() + "/progress")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.level").value("STAFF"))
                .andExpect(jsonPath("$.progressPercent").value(95));
    }
}
