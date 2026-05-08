package com.tunisales.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tunisales.inventory.client.PlatformNotificationClient;
import com.tunisales.inventory.domain.StockItem;
import com.tunisales.inventory.domain.StockMovement;
import com.tunisales.inventory.domain.Warehouse;
import com.tunisales.inventory.domain.enumeration.MovementType;
import com.tunisales.inventory.domain.enumeration.StockItemStatus;
import com.tunisales.inventory.domain.enumeration.WarehouseType;
import com.tunisales.inventory.repository.StockItemRepository;
import com.tunisales.inventory.repository.StockMovementRepository;
import com.tunisales.inventory.repository.WarehouseRepository;
import com.tunisales.inventory.service.dto.StockItemDTO;
import com.tunisales.inventory.service.mapper.StockItemMapper;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit tests for the Étape 1.2 transition methods on {@link StockItemService}.
 */
@ExtendWith(MockitoExtension.class)
class StockItemServiceTest {

    @Mock
    private StockItemRepository stockItemRepository;

    @Mock
    private StockItemMapper stockItemMapper;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @Mock
    private PlatformNotificationClient notificationClient;

    @InjectMocks
    private StockItemService stockItemService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(stockItemService, "defaultSiteWarehouseName", "MAIN_SITE");
    }

    @Test
    void declareLost_failsWhenNotMissing() {
        StockItem item = new StockItem().id(1L).status(StockItemStatus.AVAILABLE);
        when(stockItemRepository.findOneWithEagerRelationships(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> stockItemService.declareLost(1L, "alice", "stolen")).isInstanceOf(IllegalStateException.class);
        verify(stockItemRepository, never()).save(any());
    }

    @Test
    void declareLost_succeedsFromMissing() {
        StockItem item = new StockItem().id(1L).status(StockItemStatus.MISSING);
        when(stockItemRepository.findOneWithEagerRelationships(1L)).thenReturn(Optional.of(item));
        when(stockItemRepository.save(any(StockItem.class))).thenAnswer(inv -> inv.getArgument(0));
        when(stockItemMapper.toDto(any(StockItem.class))).thenReturn(new StockItemDTO());

        stockItemService.declareLost(1L, "alice", "stolen");

        ArgumentCaptor<StockItem> itemCaptor = ArgumentCaptor.forClass(StockItem.class);
        verify(stockItemRepository).save(itemCaptor.capture());
        assertThat(itemCaptor.getValue().getStatus()).isEqualTo(StockItemStatus.LOST);

        ArgumentCaptor<StockMovement> movCaptor = ArgumentCaptor.forClass(StockMovement.class);
        verify(stockMovementRepository).save(movCaptor.capture());
        assertThat(movCaptor.getValue().getMovementType()).isEqualTo(MovementType.INVENTORY_ADJUSTMENT);
        assertThat(movCaptor.getValue().getReason()).contains("LOST");
    }

    @Test
    void markRepaired_failsWhenNotDefective() {
        StockItem item = new StockItem().id(1L).status(StockItemStatus.AVAILABLE);
        when(stockItemRepository.findOneWithEagerRelationships(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> stockItemService.markRepaired(1L)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void markRepaired_movesItemToConfiguredSiteWarehouse() {
        Warehouse defectiveWh = new Warehouse().id(99L).type(WarehouseType.DEFECTIVE);
        Warehouse main = new Warehouse().id(20L).name("MAIN_SITE").type(WarehouseType.SITE).isActive(true);
        StockItem item = new StockItem().id(1L).status(StockItemStatus.DEFECTIVE).warehouse(defectiveWh);
        when(stockItemRepository.findOneWithEagerRelationships(1L)).thenReturn(Optional.of(item));
        when(warehouseRepository.findFirstByNameAndIsActiveTrue("MAIN_SITE")).thenReturn(Optional.of(main));
        when(stockItemRepository.save(any(StockItem.class))).thenAnswer(inv -> inv.getArgument(0));
        when(stockItemMapper.toDto(any(StockItem.class))).thenReturn(new StockItemDTO());

        stockItemService.markRepaired(1L);

        ArgumentCaptor<StockItem> itemCaptor = ArgumentCaptor.forClass(StockItem.class);
        verify(stockItemRepository).save(itemCaptor.capture());
        assertThat(itemCaptor.getValue().getStatus()).isEqualTo(StockItemStatus.AVAILABLE);
        assertThat(itemCaptor.getValue().getWarehouse().getId()).isEqualTo(20L);

        ArgumentCaptor<StockMovement> movCaptor = ArgumentCaptor.forClass(StockMovement.class);
        verify(stockMovementRepository).save(movCaptor.capture());
        assertThat(movCaptor.getValue().getMovementType()).isEqualTo(MovementType.TRANSFER);
        assertThat(movCaptor.getValue().getToWarehouse().getId()).isEqualTo(20L);
    }

    @Test
    void markRepaired_fallsBackToFirstActiveSiteWarehouse() {
        Warehouse defectiveWh = new Warehouse().id(99L).type(WarehouseType.DEFECTIVE);
        Warehouse fallback = new Warehouse().id(33L).name("BACKUP").type(WarehouseType.SITE).isActive(true);
        StockItem item = new StockItem().id(1L).status(StockItemStatus.DEFECTIVE).warehouse(defectiveWh);
        when(stockItemRepository.findOneWithEagerRelationships(1L)).thenReturn(Optional.of(item));
        when(warehouseRepository.findFirstByNameAndIsActiveTrue("MAIN_SITE")).thenReturn(Optional.empty());
        when(warehouseRepository.findFirstByTypeAndIsActiveTrueOrderByIdAsc(WarehouseType.SITE)).thenReturn(Optional.of(fallback));
        when(stockItemRepository.save(any(StockItem.class))).thenAnswer(inv -> inv.getArgument(0));
        when(stockItemMapper.toDto(any(StockItem.class))).thenReturn(new StockItemDTO());

        stockItemService.markRepaired(1L);

        ArgumentCaptor<StockItem> itemCaptor = ArgumentCaptor.forClass(StockItem.class);
        verify(stockItemRepository).save(itemCaptor.capture());
        assertThat(itemCaptor.getValue().getWarehouse().getId()).isEqualTo(33L);
    }

    @Test
    void markMissing_publishesNotification() {
        StockItem item = new StockItem().id(1L).status(StockItemStatus.AVAILABLE).imei("123456789012347");
        when(stockItemRepository.findOneWithEagerRelationships(1L)).thenReturn(Optional.of(item));
        when(stockItemRepository.save(any(StockItem.class))).thenAnswer(inv -> inv.getArgument(0));
        when(stockItemMapper.toDto(any(StockItem.class))).thenReturn(new StockItemDTO());

        stockItemService.markMissing(1L, "alice", "lost during transfer");

        verify(notificationClient).publish(any());
    }
}
