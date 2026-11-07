package dev.midnightcoder.examples;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

/**
 * @author Glabay | The Midnight Coder
 * @project midnight-dashboard
 * @social Discord: Glabay
 * @website <a href="https://midnightcoder.dev">Midnight Coder</a>
 * @since 2026-11-06
 */
public final class StructuredConcurrencyAllUntil {

    List<Object> invokeWithEarlyStop(List<Callable<String>> tasks) throws InterruptedException {
        try (
            var scope = StructuredTaskScope.open(
                StructuredTaskScope.Joiner.allUntil(
                    subtask -> subtask.state() == Subtask.State.FAILED)
            )
        ) {
            tasks.forEach(scope::fork);

            // Returns when all complete OR the first failure occurs
            var allSubtasks = scope.join();

            // Filter to only the successful ones
            return allSubtasks.stream()
                .filter(s -> s.state() == Subtask.State.SUCCESS)
                .map(Subtask::get)
                .toList();
        }
    }
}
