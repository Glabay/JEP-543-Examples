package dev.midnightcoder.examples;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.TimeoutException;

/**
 * @author Glabay | The Midnight Coder
 * @project midnight-dashboard
 * @social Discord: Glabay
 * @website <a href="https://midnightcoder.dev">Midnight Coder</a>
 * @since 2026-11-06
 */
final class StructuredConcurrencyTimeout {
    List<String> fetchWithTimeout(List<URI> endpoints, Duration timeout) throws InterruptedException {
        try (var scope = StructuredTaskScope.open(
                StructuredTaskScope.Joiner.<String>allSuccessfulOrThrow(),
                cf -> cf.withTimeout(timeout))
        ) {
            for (var uri : endpoints) {
                scope.fork(() -> httpClientGetString(uri));
            }

            return scope.join().stream()
                .map(Object::toString)
                .toList();

        }
        catch (ExecutionException e) {
            throw new RuntimeException("Request timed out after " + timeout, e);
        }
    }

    String httpClientGetString(URI uri) {
        return "Response from " + uri;
    }
}
