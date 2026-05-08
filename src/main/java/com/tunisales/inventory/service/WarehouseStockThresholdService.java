package com.tunisales.inventory.service;

import com.tunisales.inventory.client.PlatformNotificationClient;
import com.tunisales.inventory.domain.Warehouse;
import com.tunisales.inventory.repository.StockItemRepository;
import com.tunisales.inventory.service.dto.NotificationPayloadDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Helper that checks a {@link Warehouse}'s remaining stock against its
 * {@code minThreshold} and emits a {@code STOCK_LOW} notification when the
 * threshold is crossed downwards.
 *
 * <p>Delivery is best-effort: notification failures must never break the
 * originating inventory transaction (cf. Étape 1.6 spec).</p>
 */
@Service
@Transactional(readOnly = true)
public class WarehouseStockThresholdService {

    private static final Logger log = LoggerFactory.getLogger(WarehouseStockThresholdService.class);

    private final StockItemRepository stockItemRepository;
    private final PlatformNotificationClient notificationClient;

    public WarehouseStockThresholdService(StockItemRepository stockItemRepository, PlatformNotificationClient notificationClient) {
        this.stockItemRepository = stockItemRepository;
        this.notificationClient = notificationClient;
    }

    /**
     * Re-count items currently sitting in {@code warehouse} and emit a
     * {@code STOCK_LOW} notification if the count falls below the configured
     * {@code minThreshold}. Returns silently when no threshold is set.
     */
    public void checkAndNotify(Warehouse warehouse) {
        if (warehouse == null || warehouse.getMinThreshold() == null) {
            return;
        }
        long currentCount;
        try {
            currentCount = countItemsInWarehouse(warehouse);
        } catch (Exception ex) {
            log.warn("Failed to count items in warehouse {}: {}", warehouse.getId(), ex.getMessage());
            return;
        }
        if (currentCount < warehouse.getMinThreshold()) {
            try {
                notificationClient.publish(
                    new NotificationPayloadDTO(
                        "*ADMIN_COMMERCIAL*",
                        "STOCK_LOW",
                        "Low stock alert",
                        "Warehouse '" +
                        warehouse.getName() +
                        "' is below its minimum threshold (" +
                        currentCount +
                        " < " +
                        warehouse.getMinThreshold() +
                        ").",
                        "{\"warehouseId\":" +
                        warehouse.getId() +
                        ",\"current\":" +
                        currentCount +
                        ",\"threshold\":" +
                        warehouse.getMinThreshold() +
                        "}"
                    )
                );
            } catch (Exception ex) {
                log.warn("Failed to publish STOCK_LOW notification for warehouse {}: {}", warehouse.getId(), ex.getMessage());
            }
        }
    }

    private long countItemsInWarehouse(Warehouse warehouse) {
        Specification<com.tunisales.inventory.domain.StockItem> spec = (root, q, cb) ->
            cb.and(cb.equal(root.get("warehouse").get("id"), warehouse.getId()), cb.equal(root.get("isDeleted"), Boolean.FALSE));
        return stockItemRepository.count(spec);
    }
}
