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
public final class StructuredConcurrencyRunnable {
    value record NotificationEvent(String to, String subject, String body) {}

    void sendNotifications(NotificationEvent event) throws InterruptedException {
        try (var scope = StructuredTaskScope.open(StructuredTaskScope.Joiner.awaitAllSuccessfulOrThrow())) {

            scope.fork(() -> emailServiceSend(event.to(), event.subject(), event.body()));
            scope.fork(() -> smsServiceSend(event.to(), event.body()));
            scope.fork(() -> pushServiceSend(event.to(), event.body()));

            scope.join(); // Waits for all; throws if any fails
        }
        catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    void emailServiceSend(String to, String subject, String body) {
        // Send email
    }

    void smsServiceSend(String to, String body) {
        // Send SMS
    }

    void pushServiceSend(String to, String body) {
        // Send push notification
    }
}
