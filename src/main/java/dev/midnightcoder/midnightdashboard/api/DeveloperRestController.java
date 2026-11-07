package dev.midnightcoder.midnightdashboard.api;

import dev.midnightcoder.midnightdashboard.developer.IDeveloperService;
import dev.midnightcoder.midnightdashboard.developer.dto.CreateDeveloperRequest;
import dev.midnightcoder.midnightdashboard.developer.dto.ProgressUpdateRequest;
import dev.midnightcoder.midnightdashboard.developer.internal.Developer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/developers")
public class DeveloperRestController {
    private final IDeveloperService developerService;

    @GetMapping
    public List<Developer> findDevelopers(
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String team,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search
    ) {
        return developerService.findDevelopers(level, team, status, search);
    }

    @GetMapping("/{id}")
    public Developer findDeveloper(@PathVariable long id) {
        return developerService.findDeveloper(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Developer createDeveloper(@Valid @RequestBody CreateDeveloperRequest request) {
        return developerService.createDeveloper(request);
    }

    @PatchMapping("/{id}/progress")
    public Developer updateProgress(
            @PathVariable long id,
            @Valid @RequestBody ProgressUpdateRequest request
    ) {
        return developerService.updateProgress(id, request);
    }
}
