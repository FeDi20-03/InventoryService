package com.tunisales.inventory.service.scheduler;

import com.tunisales.inventory.client.PlatformNotificationClient;
import com.tunisales.inventory.domain.StockAudit;
import com.tunisales.inventory.domain.Warehouse;
import com.tunisales.inventory.domain.enumeration.AuditMode;
import com.tunisales.inventory.domain.enumeration.AuditStatus;
import com.tunisales.inventory.domain.enumeration.WarehouseType;
import com.tunisales.inventory.repository.StockAuditRepository;
import com.tunisales.inventory.repository.WarehouseRepository;
import com.tunisales.inventory.service.dto.NotificationPayloadDTO;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Periodic scheduler that creates {@link StockAudit} entries for the standard
 * audit cadence:
 * <ul>
 *   <li><b>Weekly</b> for every active LOCAL warehouse (commercial vehicles).</li>
 *   <li><b>Monthly</b> for every active SITE warehouse (main depots).</li>
 * </ul>
 *
 * <p>Each scheduled audit is persisted with status {@link AuditStatus#SCHEDULED}
 * and audit mode {@link AuditMode#COUNT}; a {@code AUDIT_SCHEDULED} notification
 * is emitted to the magasinier so they can pick it up from the audit dashboard.</p>
 *
 * <p>The cron expressions are configurable via {@code tunisales.inventory.audit.local-cron}
 * and {@code tunisales.inventory.audit.site-cron}. A {@link Clock} is injected
 * to allow deterministic unit tests of the scheduling logic.</p>
 */
@Component
public class StockAuditScheduler {

    private static final Logger log = LoggerFactory.getLogger(StockAuditScheduler.class);

    private static final String AUDITOR_LOGIN_PLACEHOLDER = "system";

    private final WarehouseRepository warehouseRepository;
    private final StockAuditRepository stockAuditRepository;
    private final PlatformNotificationClient notificationClient;
    private final Clock clock;

    public StockAuditScheduler(
        WarehouseRepository warehouseRepository,
        StockAuditRepository stockAuditRepository,
        PlatformNotificationClient notificationClient,
        Clock clock
    ) {
        this.warehouseRepository = warehouseRepository;
        this.stockAuditRepository = stockAuditRepository;
        this.notificationClient = notificationClient;
        this.clock = clock;
    }

    /**
     * Schedule weekly audits for every active LOCAL warehouse (Monday 06:00 by default).
     */
    @Scheduled(cron = "${tunisales.inventory.audit.local-cron:0 0 6 * * MON}")
    @Transactional
    public void scheduleLocalAudits() {
        log.info("Running weekly LOCAL warehouse audit scheduler");
        scheduleAuditsFor(WarehouseType.LOCAL);
    }

    /**
     * Schedule monthly audits for every active SITE warehouse (1st of the month, 06:00 by default).
     */
    @Scheduled(cron = "${tunisales.inventory.audit.site-cron:0 0 6 1 * *}")
    @Transactional
    public void scheduleSiteAudits() {
        log.info("Running monthly SITE warehouse audit scheduler");
        scheduleAuditsFor(WarehouseType.SITE);
    }

    /**
     * Test-friendly entry point that performs the same work as the
     * {@link Scheduled} cron triggers without depending on the cron parser.
     */
    @Transactional
    public int scheduleAuditsFor(WarehouseType type) {
        List<Warehouse> warehouses = warehouseRepository.findAllByTypeAndIsActiveTrue(type);
        int created = 0;
        for (Warehouse warehouse : warehouses) {
            StockAudit audit = new StockAudit()
                .tenantId(warehouse.getTenantId())
                .status(AuditStatus.SCHEDULED)
                .auditMode(AuditMode.COUNT)
                .auditorLogin(AUDITOR_LOGIN_PLACEHOLDER)
                .startedAt(ZonedDateTime.now(clock))
                .warehouse(warehouse);
            audit = stockAuditRepository.save(audit);
            created++;

            try {
                notificationClient.publish(
                    new NotificationPayloadDTO(
                        "*MAGASINIER*",
                        "AUDIT_SCHEDULED",
                        "Stock audit scheduled",
                        "A new " + type + " audit has been scheduled for warehouse '" + warehouse.getName() + "'.",
                        "{\"auditId\":" + audit.getId() + ",\"warehouseId\":" + warehouse.getId() + ",\"type\":\"" + type + "\"}"
                    )
                );
            } catch (Exception ex) {
                log.warn("Failed to publish AUDIT_SCHEDULED notification for warehouse {}: {}", warehouse.getId(), ex.getMessage());
            }
        }
        log.info("Scheduled {} audits for warehouse type {}", created, type);
        return created;
    }
}
