package dev.midnightcoder.examples;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

/**
 * @author Glabay | The Midnight Coder
 * @project midnight-dashboard
 * @social Discord: Glabay
 * @website <a href="https://midnightcoder.dev">Midnight Coder</a>
 * @since 2026-11-06
 */
final class StructuredConcurrencyAllSuccessful {
    List<String> fetchAllEndpoints(List<URI> endpoints) throws InterruptedException {
        try (var scope = StructuredTaskScope.open(
            StructuredTaskScope.Joiner.allSuccessfulOrThrow())
        ) {
            for (var uri : endpoints) {
                scope.fork(() -> httpClientGetString(uri));
            }

            return scope.join()
                .stream()
                .map(Object::toString)
                .toList();
        }
        catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    String httpClientGetString(URI uri) {
        return "Response from " + uri;
    }
}
