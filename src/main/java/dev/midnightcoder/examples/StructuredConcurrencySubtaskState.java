package dev.midnightcoder.examples;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

/**
 * @author Glabay | The Midnight Coder
 * @project midnight-dashboard
 * @social Discord: Glabay
 * @website <a href="https://midnightcoder.dev">Midnight Coder</a>
 * @since 2026-11-06
 */
final class StructuredConcurrencySubtaskState {
    value record EndpointReport(URI endpoint, String status) {}

    List<EndpointReport> reportEndpointHealth(List<URI> endpoints) throws InterruptedException {
        var reports = new ArrayList<EndpointReport>();

        try (var scope = StructuredTaskScope.open(
            // Wait for all, don't cancel early
            StructuredTaskScope.Joiner.allUntil(_ -> false))
        ) {
            var subtasks = new ArrayList<Subtask<String>>();
            for (var uri : endpoints) {
                subtasks.add(scope.fork(() -> httpClientGetString(uri)));
            }

            scope.join();

            for (int i = 0; i < subtasks.size(); i++) {
                var subtask = subtasks.get(i);
                var uri = endpoints.get(i);
                var status = switch (subtask.state()) {
                    case SUCCESS -> "OK";
                    case FAILED -> "ERROR: " + subtask.exception().getMessage();
                    case UNAVAILABLE -> "CANCELLED";
                };
                reports.add(new EndpointReport(uri, status));
            }
        }
        return reports;
    }

    String httpClientGetString(URI uri) {
        return "Response from " + uri;
    }
}
