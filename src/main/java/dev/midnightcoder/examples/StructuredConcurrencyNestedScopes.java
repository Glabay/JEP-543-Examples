package dev.midnightcoder.examples;

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
public final class StructuredConcurrencyNestedScopes {
    public value record DashboardData(CustomerData customer, Orders orders, Notifications notifications) {}
    public value record CustomerData(Profile profile, Preferences preferences) {}
    public value record Profile(String name, String email) {}
    public value record Preferences(String theme, String language) {}
    public value record Orders(List<String> items) {}
    public value record Notifications(List<String> messages) {}

    DashboardData fetchDashboard(long customerId) throws InterruptedException, ExecutionException {
        try (var scope = StructuredTaskScope.open(StructuredTaskScope.Joiner.allSuccessfulOrThrow())) {

            // Top-level subtask that itself forks more work
            var customer = scope.fork(() -> {
                try (var inner = StructuredTaskScope.open()) {
                    var profile = inner.fork(() -> fetchCustomerProfile(customerId));
                    var preferences = inner.fork(() -> fetchCustomerPreferences(customerId));
                    inner.join();
                    return new CustomerData(profile.get(), preferences.get());
                }
            });

            var orders = scope.fork(() -> fetchRecentOrders(customerId));
            var notifications = scope.fork(() -> fetchNotifications(customerId));

            scope.join();
            return new DashboardData(customer.get(), orders.get(), notifications.get());
        }
    }

    Profile fetchCustomerProfile(long id) {
        return new Profile("Alice", "alice@example.com");
    }

    Preferences fetchCustomerPreferences(long id) {
        return new Preferences("dark", "en");
    }

    Orders fetchRecentOrders(long customerId) {
        return new Orders(List.of("Order-1", "Order-2"));
    }

    Notifications fetchNotifications(long customerId) {
        return new Notifications(List.of("Welcome!", "New feature available"));
    }
}
