package dev.midnightcoder.midnightdashboard.api;

import dev.midnightcoder.midnightdashboard.dashboard.DashboardService;
import dev.midnightcoder.midnightdashboard.dashboard.DashboardSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboard")
public class DashboardRestController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummary getSummary() {
        return dashboardService.getSummary();
    }
}
