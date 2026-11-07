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
final class StructuredConcurrencyThreadNaming {
    value record ProfileResponse(User user, Order lastOrder) {}
    value record User(long id, String name) {}
    value record Order(long id, String item) {}

    ProfileResponse fetchProfile(long userId) throws InterruptedException, ExecutionException {
        var factory = Thread.ofVirtual()
                .name("profile-", 0)
                .factory();

        try (var scope = StructuredTaskScope.open(
                StructuredTaskScope.Joiner.awaitAllSuccessfulOrThrow(),
                cf -> cf.withThreadFactory(factory))) {

            var user = scope.fork(() -> findUser(userId));    // "profile-0"
            var order = scope.fork(() -> fetchLastOrder(userId)); // "profile-1"

            scope.join();
            return new ProfileResponse(user.get(), order.get());
        }
    }

    User findUser(long id) {
        return new User(id, "Alice");
    }

    Order fetchLastOrder(long userId) {
        return new Order(1L, "Widget");
    }
}
