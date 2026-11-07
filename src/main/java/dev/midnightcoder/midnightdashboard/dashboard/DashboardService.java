package dev.midnightcoder.midnightdashboard.dashboard;

import dev.midnightcoder.midnightdashboard.developer.internal.DeveloperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final DeveloperRepository developerRepository;

    public DashboardSummary getSummary() {
        try (var scope = StructuredTaskScope.open()) {
            var total = scope.fork(developerRepository::countAll);
            var active = scope.fork(developerRepository::countActive);
            var onLeave = scope.fork(developerRepository::countOnLeave);
            var inactive = scope.fork(developerRepository::countInactive);
            var averageProgress = scope.fork(developerRepository::averageProgress);
            var levels = scope.fork(developerRepository::countByLevel);
            var teams = scope.fork(developerRepository::countByTeam);

            scope.join();

            return new DashboardSummary(
                total.get(),
                active.get(),
                onLeave.get(),
                inactive.get(),
                averageProgress.get(),
                levels.get(),
                teams.get()
            );
        }
        catch (ExecutionException e) {
            if (e.getCause() instanceof InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(
                    "Dashboard aggregation was interrupted", ie);
            }
            throw new IllegalStateException("Dashboard aggregation failed",
                                            e.getCause());
        }
        catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
