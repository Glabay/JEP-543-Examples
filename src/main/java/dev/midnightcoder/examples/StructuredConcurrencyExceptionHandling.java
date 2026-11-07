package dev.midnightcoder.examples;

import java.io.IOException;
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
public final class StructuredConcurrencyExceptionHandling {
    public value record ProfileResponse(User user, Order lastOrder) {}
    public value record User(long id, String name) {}
    public value record Order(long id, String item) {}

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(long id) {
            super("User not found: " + id);
        }
    }

    ProfileResponse fetchProfile(long userId) throws InterruptedException {
        try (var scope = StructuredTaskScope.open()) {
            var user = scope.fork(() -> findUser(userId));
            var order = scope.fork(() -> fetchLastOrder(userId));

            scope.join();
            return new ProfileResponse(user.get(), order.get());

        }
        catch (ExecutionException e) {
            throw switch (e.getCause()) {
                case TimeoutException _ -> new RuntimeException("Upstream timeout", e);
                case IOException io -> new RuntimeException("Network error: " + io.getMessage(), e);
                case UserNotFoundException _ -> new RuntimeException("User not found: " + userId, e);
                case Throwable throwable -> new RuntimeException("Subtask failed", throwable);
                case null -> new RuntimeException("Subtask failed");
            };
        }
    }

    User findUser(long id) {
        return new User(id, "Alice");
    }

    Order fetchLastOrder(long userId) {
        return new Order(1L, "Widget");
    }
}
