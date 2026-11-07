package dev.midnightcoder.examples;

import java.time.Duration;
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
public final class StructuredConcurrencyDynamicForking {
    public value record ProductId(String value) {}
    public value record SupplierId(String value) {}
    public value record DeliveryTime(SupplierId supplier, Duration estimated) {}

    List<DeliveryTime> getSupplierDeliveryTimes(
            List<ProductId> productIds,
            List<SupplierId> supplierIds,
            Duration timeout) throws InterruptedException, ExecutionException {

        try (var scope = StructuredTaskScope.open(
                StructuredTaskScope.Joiner.allSuccessfulOrThrow(),
                cf -> cf.withTimeout(timeout))) {

            for (var productId : productIds) {
                scope.fork(() -> getSupplierDeliveryTime(productId, supplierIds));
            }

            return scope.join()
                .stream()
                .map(DeliveryTime.class::cast)
                .toList();
        }
    }

    DeliveryTime getSupplierDeliveryTime(ProductId productId, List<SupplierId> supplierIds) {
        return new DeliveryTime(supplierIds.getFirst(), Duration.ofDays(3));
    }
}
