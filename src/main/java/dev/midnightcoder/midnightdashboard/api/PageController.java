package dev.midnightcoder.midnightdashboard.api;

import dev.midnightcoder.midnightdashboard.dashboard.DashboardService;
import dev.midnightcoder.midnightdashboard.developer.IDeveloperService;
import dev.midnightcoder.midnightdashboard.developer.internal.DeveloperLevel;
import dev.midnightcoder.midnightdashboard.developer.internal.DeveloperStatus;
import dev.midnightcoder.midnightdashboard.developer.internal.DeveloperTeam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Arrays;

@Controller
@RequiredArgsConstructor
public class PageController {
    private final DashboardService dashboardService;
    private final IDeveloperService developerService;

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("summary", dashboardService.getSummary());
        model.addAttribute("developers", developerService.findDevelopers(null, null, null, null));
        model.addAttribute("levels", Arrays.asList(DeveloperLevel.values()));
        model.addAttribute("teams", Arrays.asList(DeveloperTeam.values()));
        model.addAttribute("statuses", Arrays.asList(DeveloperStatus.values()));
        return "dashboard";
    }

    @GetMapping("/developers/{id}")
    public String developerDetails(@PathVariable long id, Model model) {
        model.addAttribute("developer", developerService.findDeveloper(id));
        return "developer-detail";
    }
}
