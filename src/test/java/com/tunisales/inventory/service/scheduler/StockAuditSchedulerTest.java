package com.tunisales.inventory.service.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link StockAuditScheduler}. Uses a fixed {@link Clock} so the
 * {@code startedAt} timestamp is deterministic.
 */
@ExtendWith(MockitoExtension.class)
class StockAuditSchedulerTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private StockAuditRepository stockAuditRepository;

    @Mock
    private PlatformNotificationClient notificationClient;

    private final Clock fixedClock = Clock.fixed(Instant.parse("2026-05-07T06:00:00Z"), ZoneId.of("UTC"));

    @InjectMocks
    private StockAuditScheduler scheduler;

    StockAuditSchedulerTest() {
        // Re-init with the fixed clock manually because @InjectMocks would pass null.
    }

    private StockAuditScheduler buildScheduler() {
        return new StockAuditScheduler(warehouseRepository, stockAuditRepository, notificationClient, fixedClock);
    }

    @Test
    void scheduleLocalAudits_createsOneAuditPerActiveLocalWarehouse() {
        Warehouse car1 = new Warehouse().id(1L).tenantId(100L).name("CAR_ALICE").type(WarehouseType.LOCAL).isActive(true);
        Warehouse car2 = new Warehouse().id(2L).tenantId(100L).name("CAR_BOB").type(WarehouseType.LOCAL).isActive(true);
        when(warehouseRepository.findAllByTypeAndIsActiveTrue(WarehouseType.LOCAL)).thenReturn(List.of(car1, car2));
        AtomicLong idGen = new AtomicLong(1);
        when(stockAuditRepository.save(any(StockAudit.class)))
            .thenAnswer(inv -> {
                StockAudit a = inv.getArgument(0);
                a.setId(idGen.getAndIncrement());
                return a;
            });

        int created = buildScheduler().scheduleAuditsFor(WarehouseType.LOCAL);

        assertThat(created).isEqualTo(2);
        ArgumentCaptor<StockAudit> auditCaptor = ArgumentCaptor.forClass(StockAudit.class);
        verify(stockAuditRepository, times(2)).save(auditCaptor.capture());
        for (StockAudit saved : auditCaptor.getAllValues()) {
            assertThat(saved.getStatus()).isEqualTo(AuditStatus.SCHEDULED);
            assertThat(saved.getAuditMode()).isEqualTo(AuditMode.COUNT);
            assertThat(saved.getStartedAt()).isNotNull();
        }

        ArgumentCaptor<NotificationPayloadDTO> notifCaptor = ArgumentCaptor.forClass(NotificationPayloadDTO.class);
        verify(notificationClient, times(2)).publish(notifCaptor.capture());
        for (NotificationPayloadDTO n : notifCaptor.getAllValues()) {
            assertThat(n.getType()).isEqualTo("AUDIT_SCHEDULED");
            assertThat(n.getRecipientLogin()).contains("MAGASINIER");
        }
    }

    @Test
    void scheduleSiteAudits_skipsInactiveWarehouses() {
        Warehouse main = new Warehouse().id(10L).tenantId(100L).name("MAIN_SITE").type(WarehouseType.SITE).isActive(true);
        when(warehouseRepository.findAllByTypeAndIsActiveTrue(WarehouseType.SITE)).thenReturn(List.of(main));
        when(stockAuditRepository.save(any(StockAudit.class)))
            .thenAnswer(inv -> {
                StockAudit a = inv.getArgument(0);
                a.setId(1L);
                return a;
            });

        int created = buildScheduler().scheduleAuditsFor(WarehouseType.SITE);

        assertThat(created).isEqualTo(1);
    }

    @Test
    void notificationFailureIsSwallowed() {
        Warehouse w = new Warehouse().id(1L).tenantId(100L).name("CAR_X").type(WarehouseType.LOCAL).isActive(true);
        when(warehouseRepository.findAllByTypeAndIsActiveTrue(WarehouseType.LOCAL)).thenReturn(List.of(w));
        when(stockAuditRepository.save(any(StockAudit.class)))
            .thenAnswer(inv -> {
                StockAudit a = inv.getArgument(0);
                a.setId(1L);
                return a;
            });
        org.mockito.Mockito.doThrow(new RuntimeException("platform down")).when(notificationClient).publish(any());

        int created = buildScheduler().scheduleAuditsFor(WarehouseType.LOCAL);
        assertThat(created).isEqualTo(1);
    }
}
