package dev.midnightcoder.midnightdashboard.developer.internal;

import dev.midnightcoder.midnightdashboard.developer.IDeveloperService;
import dev.midnightcoder.midnightdashboard.developer.dto.CreateDeveloperRequest;
import dev.midnightcoder.midnightdashboard.developer.dto.ProgressUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeveloperService implements IDeveloperService {
    private final DeveloperRepository developerRepository;

    public List<Developer> findDevelopers(String level, String team, String status, String search) {
        return developerRepository.findAll(normalize(level), normalize(team), normalize(status), search);
    }

    public Developer findDeveloper(long id) {
        return developerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Developer not found: " + id));
    }

    @Transactional
    public Developer createDeveloper(CreateDeveloperRequest request) {
        var id = developerRepository.create(request);
        return findDeveloper(id);
    }

    @Transactional
    public Developer updateProgress(long id, ProgressUpdateRequest request) {
        if (!developerRepository.updateProgress(id, request)) {
            throw new ResponseStatusException(NOT_FOUND, "Developer not found: " + id);
        }
        return findDeveloper(id);
    }
}
