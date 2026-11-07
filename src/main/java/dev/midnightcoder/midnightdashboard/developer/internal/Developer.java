package dev.midnightcoder.midnightdashboard.developer.internal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Locale;

@Getter
@Setter
@Entity
@Builder
@Table(name = "developers")
@AllArgsConstructor
public class Developer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 180)
    private String email;

    @Column(name = "github_username", nullable = false, unique = true, length = 39)
    private String githubUsername;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false, length = 20)
    private DeveloperLevel level;

    @Enumerated(EnumType.STRING)
    @Column(name = "team", nullable = false, length = 20)
    private DeveloperTeam team;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DeveloperStatus status;

    @Column(name = "progress_percent", nullable = false)
    private int progressPercent;

    @Column(name = "experience_years", nullable = false, precision = 4, scale = 1)
    private BigDecimal experienceYears;

    @Column(name = "joined_on", nullable = false)
    private LocalDate joinedOn;

    @Column(name = "last_active_at", nullable = false)
    private OffsetDateTime lastActiveAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id")
    @JsonIgnore
    private Developer mentor;

    @Column(name = "bio", length = 1000)
    private String bio;

    @Column(name = "created_at")
    @JsonIgnore
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    @JsonIgnore
    private OffsetDateTime updatedAt;

    public Developer() {
    }

    public Developer(String firstName, String lastName, String email, String githubUsername,
                     DeveloperLevel level, DeveloperTeam team, DeveloperStatus status,
                     int progressPercent, BigDecimal experienceYears, LocalDate joinedOn,
                     OffsetDateTime lastActiveAt, Developer mentor, String bio) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.githubUsername = githubUsername;
        this.level = level;
        this.team = team;
        this.status = status;
        this.progressPercent = progressPercent;
        this.experienceYears = experienceYears;
        this.joinedOn = joinedOn;
        this.lastActiveAt = lastActiveAt;
        this.mentor = mentor;
        this.bio = bio;
    }

    public Developer(String firstName, String lastName, String email, String githubUsername,
                     DeveloperLevel level, DeveloperTeam team, DeveloperStatus status,
                     int progressPercent, BigDecimal experienceYears, LocalDate joinedOn,
                     Developer mentor, String bio) {
        this(firstName, lastName, email, githubUsername, level, team, status,
             progressPercent, experienceYears, joinedOn, OffsetDateTime.now(), mentor, bio);
    }


    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        if (this.lastActiveAt == null) {
            this.lastActiveAt = now;
        }
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public String getFullName() {
        if (firstName == null && lastName == null) {
            return "";
        }
        if (firstName == null) {
            return lastName;
        }
        if (lastName == null) {
            return firstName;
        }
        return firstName + " " + lastName;
    }

    public String getInitials() {
        if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty()) {
            return "";
        }
        return (firstName.charAt(0) + lastName.substring(0, 1)).toUpperCase(Locale.ROOT);
    }

    public Long getMentorId() {
        return mentor != null ? mentor.getId() : null;
    }

    public String getMentorName() {
        return mentor != null ? mentor.getFullName() : null;
    }
}
