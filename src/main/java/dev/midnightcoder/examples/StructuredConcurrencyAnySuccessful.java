package dev.midnightcoder.examples;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

/**
 * @author Glabay | The Midnight Coder
 * @project midnight-dashboard
 * @social Discord: Glabay
 * @website <a href="https://midnightcoder.dev">Midnight Coder</a>
 * @since 2026-11-06
 */
public final class StructuredConcurrencyAnySuccessful {
    public value record Address(String street, String city) {}

    String verifyAddress(Address address) throws InterruptedException, ExecutionException {
        try (var scope = StructuredTaskScope.open(StructuredTaskScope.Joiner.anySuccessfulOrThrow())) {
            scope.fork(() -> verifyWithProviderA(address));
            scope.fork(() -> verifyWithProviderB(address));
            scope.fork(() -> verifyWithProviderC(address));

            // Returns the result of the first subtask to succeed
            // The other two are automatically canceled
            return scope.join()
                .toString();
        }
    }

    String verifyWithProviderA(Address address) {
        return "ProviderA: " + address.street();
    }

    String verifyWithProviderB(Address address) {
        return "ProviderB: " + address.street();
    }

    String verifyWithProviderC(Address address) {
        return "ProviderC: " + address.street();
    }
}
