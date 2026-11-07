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
public final class StructuredConcurrencyBasic {

    public value record ProfileResponse(User user, Order lastOrder) {}
    public value record User(long id, String name) {}
    public value record Order(long id, String item) {}

    ProfileResponse fetchProfile(long userId) throws InterruptedException, ExecutionException {
        try (var scope = StructuredTaskScope.open()) {
            var user = scope.fork(() -> findUser(userId));
            var order = scope.fork(() -> fetchLastOrder(userId));

            // Blocks until both complete or one fails
            scope.join(); // throws ExecutionException if either subtask fails

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
