package com.tunisales.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tunisales.inventory.client.PlatformNotificationClient;
import com.tunisales.inventory.domain.StockAudit;
import com.tunisales.inventory.domain.StockAuditLine;
import com.tunisales.inventory.domain.StockItem;
import com.tunisales.inventory.domain.Warehouse;
import com.tunisales.inventory.domain.enumeration.AuditMode;
import com.tunisales.inventory.domain.enumeration.AuditResolution;
import com.tunisales.inventory.domain.enumeration.AuditStatus;
import com.tunisales.inventory.domain.enumeration.WarehouseType;
import com.tunisales.inventory.repository.StockAuditLineRepository;
import com.tunisales.inventory.repository.StockAuditRepository;
import com.tunisales.inventory.repository.StockItemRepository;
import com.tunisales.inventory.service.dto.NotificationPayloadDTO;
import com.tunisales.inventory.service.dto.StockAuditDTO;
import com.tunisales.inventory.service.mapper.StockAuditMapper;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the Étape 1.3 audit-workflow extensions on
 * {@link StockAuditService}.
 */
@ExtendWith(MockitoExtension.class)
class StockAuditServiceTest {

    @Mock
    private StockAuditRepository stockAuditRepository;

    @Mock
    private StockAuditMapper stockAuditMapper;

    @Mock
    private StockAuditLineRepository stockAuditLineRepository;

    @Mock
    private StockItemRepository stockItemRepository;

    @Mock
    private PlatformNotificationClient notificationClient;

    @InjectMocks
    private StockAuditService stockAuditService;

    @Test
    void recordCount_switchesToScanModeOnDiscrepancy() {
        StockAudit audit = new StockAudit().status(AuditStatus.IN_PROGRESS).theoreticalCount(10).auditMode(AuditMode.COUNT);
        when(stockAuditRepository.findById(1L)).thenReturn(Optional.of(audit));
        when(stockAuditRepository.save(any(StockAudit.class))).thenAnswer(inv -> inv.getArgument(0));
        when(stockAuditMapper.toDto(any(StockAudit.class))).thenReturn(new StockAuditDTO());

        stockAuditService.recordCount(1L, 7);

        ArgumentCaptor<StockAudit> captor = ArgumentCaptor.forClass(StockAudit.class);
        verify(stockAuditRepository).save(captor.capture());
        StockAudit saved = captor.getValue();
        assertThat(saved.getAuditMode()).isEqualTo(AuditMode.SCAN_ONE_BY_ONE);
        assertThat(saved.getPhysicalCount()).isEqualTo(7);
        assertThat(saved.getDiscrepancyCount()).isEqualTo(3);
    }

    @Test
    void recordCount_keepsCountModeWhenMatching() {
        StockAudit audit = new StockAudit().status(AuditStatus.IN_PROGRESS).theoreticalCount(10).auditMode(AuditMode.COUNT);
        when(stockAuditRepository.findById(1L)).thenReturn(Optional.of(audit));
        when(stockAuditRepository.save(any(StockAudit.class))).thenAnswer(inv -> inv.getArgument(0));
        when(stockAuditMapper.toDto(any(StockAudit.class))).thenReturn(new StockAuditDTO());

        stockAuditService.recordCount(1L, 10);

        ArgumentCaptor<StockAudit> captor = ArgumentCaptor.forClass(StockAudit.class);
        verify(stockAuditRepository).save(captor.capture());
        assertThat(captor.getValue().getAuditMode()).isEqualTo(AuditMode.COUNT);
        assertThat(captor.getValue().getDiscrepancyCount()).isEqualTo(0);
    }

    @Test
    void scanLine_marksUnexpectedWhenItemNotInPerimeter() {
        Warehouse audited = new Warehouse().id(1L).type(WarehouseType.SITE).isActive(true).name("MAIN");
        Warehouse other = new Warehouse().id(2L).type(WarehouseType.LOCAL).isActive(true).name("CAR");
        StockItem item = new StockItem().id(50L).imei("123456789012347").warehouse(other);
        StockAudit audit = new StockAudit().id(10L).warehouse(audited).status(AuditStatus.IN_PROGRESS);

        when(stockAuditRepository.findOneWithEagerRelationships(10L)).thenReturn(Optional.of(audit));
        when(stockItemRepository.findOneByImeiWithEagerRelationships("123456789012347")).thenReturn(Optional.of(item));
        when(stockAuditLineRepository.save(any(StockAuditLine.class))).thenAnswer(inv -> inv.getArgument(0));

        StockAuditLine line = stockAuditService.scanLine(10L, "123456789012347");

        assertThat(line.getResolution()).isEqualTo(AuditResolution.UNEXPECTED);
        assertThat(line.getResolutionNote()).contains("not in audited warehouse");
    }

    @Test
    void scanLine_marksFoundWhenInPerimeter() {
        Warehouse audited = new Warehouse().id(1L).type(WarehouseType.SITE).isActive(true).name("MAIN");
        StockItem item = new StockItem().id(50L).imei("123456789012347").warehouse(audited);
        StockAudit audit = new StockAudit().id(10L).warehouse(audited).status(AuditStatus.IN_PROGRESS);

        when(stockAuditRepository.findOneWithEagerRelationships(10L)).thenReturn(Optional.of(audit));
        when(stockItemRepository.findOneByImeiWithEagerRelationships("123456789012347")).thenReturn(Optional.of(item));
        when(stockAuditLineRepository.save(any(StockAuditLine.class))).thenAnswer(inv -> inv.getArgument(0));

        StockAuditLine line = stockAuditService.scanLine(10L, "123456789012347");
        assertThat(line.getResolution()).isEqualTo(AuditResolution.FOUND);
    }

    @Test
    void closeWithParallel_reopensBothWhenDiscrepancy() {
        StockAudit primary = new StockAudit().id(1L).physicalCount(8).discrepancyCount(2).status(AuditStatus.IN_PROGRESS);
        primary.setParallelOf(2L);
        StockAudit parallel = new StockAudit().id(2L).physicalCount(10).discrepancyCount(0).status(AuditStatus.IN_PROGRESS);

        when(stockAuditRepository.findById(1L)).thenReturn(Optional.of(primary));
        when(stockAuditRepository.findById(2L)).thenReturn(Optional.of(parallel));
        when(stockAuditRepository.save(any(StockAudit.class))).thenAnswer(inv -> inv.getArgument(0));
        when(stockAuditMapper.toDto(any(StockAudit.class))).thenReturn(new StockAuditDTO());

        stockAuditService.closeWithParallel(1L);

        ArgumentCaptor<StockAudit> captor = ArgumentCaptor.forClass(StockAudit.class);
        verify(stockAuditRepository, times(3)).save(captor.capture());
        // primary saved twice (close + reopen) and parallel once.
        assertThat(captor.getAllValues().get(captor.getAllValues().size() - 1).getStatus()).isEqualTo(AuditStatus.REOPENED);

        ArgumentCaptor<NotificationPayloadDTO> notifCaptor = ArgumentCaptor.forClass(NotificationPayloadDTO.class);
        verify(notificationClient).publish(notifCaptor.capture());
        assertThat(notifCaptor.getValue().getType()).isEqualTo("AUDIT_DISCREPANCY");
    }

    @Test
    void closeWithParallel_simpleCloseWhenCountsMatch() {
        StockAudit primary = new StockAudit().id(1L).physicalCount(10).discrepancyCount(0).status(AuditStatus.IN_PROGRESS);
        primary.setParallelOf(2L);
        StockAudit parallel = new StockAudit().id(2L).physicalCount(10).discrepancyCount(0).status(AuditStatus.IN_PROGRESS);

        when(stockAuditRepository.findById(1L)).thenReturn(Optional.of(primary));
        when(stockAuditRepository.findById(2L)).thenReturn(Optional.of(parallel));
        when(stockAuditRepository.save(any(StockAudit.class))).thenAnswer(inv -> inv.getArgument(0));
        when(stockAuditMapper.toDto(any(StockAudit.class))).thenReturn(new StockAuditDTO());

        stockAuditService.closeWithParallel(1L);

        verify(notificationClient, never()).publish(any());
    }
}
