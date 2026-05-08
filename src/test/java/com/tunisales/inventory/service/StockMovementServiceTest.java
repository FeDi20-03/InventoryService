package com.tunisales.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tunisales.inventory.domain.StockMovement;
import com.tunisales.inventory.domain.Warehouse;
import com.tunisales.inventory.domain.enumeration.MovementType;
import com.tunisales.inventory.domain.enumeration.WarehouseType;
import com.tunisales.inventory.repository.StockMovementRepository;
import com.tunisales.inventory.service.dto.StockMovementDTO;
import com.tunisales.inventory.service.dto.WarehouseDTO;
import com.tunisales.inventory.service.mapper.StockMovementMapper;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

/**
 * Unit tests for {@link StockMovementService} focused on Étape 1.4 logic
 * (commercial validation of vehicle alimentation).
 */
@ExtendWith(MockitoExtension.class)
class StockMovementServiceTest {

    @Mock
    private StockMovementRepository stockMovementRepository;

    @Mock
    private StockMovementMapper stockMovementMapper;

    @Mock
    private WarehouseStockThresholdService thresholdService;

    @InjectMocks
    private StockMovementService stockMovementService;

    private Warehouse localWarehouse;
    private Warehouse siteWarehouse;

    @BeforeEach
    void setup() {
        localWarehouse = new Warehouse().id(10L).name("CAR_FOO").type(WarehouseType.LOCAL).isActive(true);
        siteWarehouse = new Warehouse().id(20L).name("MAIN_SITE").type(WarehouseType.SITE).isActive(true);
    }

    @Test
    void validateByCommercial_setsFlag_whenInboundOnLocal() {
        StockMovement movement = new StockMovement()
            .id(1L)
            .movementType(MovementType.INBOUND)
            .toWarehouse(localWarehouse)
            .quantity(1)
            .createdAt(ZonedDateTime.now());
        when(stockMovementRepository.findOneWithEagerRelationships(1L)).thenReturn(Optional.of(movement));
        when(stockMovementRepository.save(any(StockMovement.class))).thenAnswer(inv -> inv.getArgument(0));
        when(stockMovementMapper.toDto(any(StockMovement.class))).thenReturn(new StockMovementDTO());

        stockMovementService.validateByCommercial(1L, "alice");

        ArgumentCaptor<StockMovement> captor = ArgumentCaptor.forClass(StockMovement.class);
        verify(stockMovementRepository).save(captor.capture());
        assertThat(captor.getValue().getConfirmedByCommercial()).isTrue();
        assertThat(captor.getValue().getPerformedByLogin()).isEqualTo("alice");
    }

    @Test
    void validateByCommercial_rejectsNonInbound() {
        StockMovement movement = new StockMovement()
            .id(1L)
            .movementType(MovementType.OUTBOUND)
            .toWarehouse(localWarehouse)
            .quantity(1)
            .createdAt(ZonedDateTime.now());
        when(stockMovementRepository.findOneWithEagerRelationships(1L)).thenReturn(Optional.of(movement));

        assertThatThrownBy(() -> stockMovementService.validateByCommercial(1L, "alice")).isInstanceOf(IllegalStateException.class);
        verify(stockMovementRepository, never()).save(any());
    }

    @Test
    void validateByCommercial_rejectsInboundOnNonLocal() {
        StockMovement movement = new StockMovement()
            .id(1L)
            .movementType(MovementType.INBOUND)
            .toWarehouse(siteWarehouse)
            .quantity(1)
            .createdAt(ZonedDateTime.now());
        when(stockMovementRepository.findOneWithEagerRelationships(1L)).thenReturn(Optional.of(movement));

        assertThatThrownBy(() -> stockMovementService.validateByCommercial(1L, "alice")).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void assertOutboundAllowed_blocksWhenLastInboundUnconfirmed() {
        StockMovement lastInbound = new StockMovement()
            .id(99L)
            .movementType(MovementType.INBOUND)
            .toWarehouse(localWarehouse)
            .quantity(1)
            .confirmedByCommercial(false)
            .createdAt(ZonedDateTime.now());
        when(
            stockMovementRepository.findLatestByToWarehouseAndType(
                org.mockito.ArgumentMatchers.eq(localWarehouse.getId()),
                org.mockito.ArgumentMatchers.eq(MovementType.INBOUND),
                any(Pageable.class)
            )
        )
            .thenReturn(List.of(lastInbound));

        assertThatThrownBy(() -> stockMovementService.assertOutboundAllowed(localWarehouse))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("not been confirmed by a commercial");
    }

    @Test
    void assertOutboundAllowed_allowsWhenLastInboundConfirmed() {
        StockMovement lastInbound = new StockMovement()
            .id(99L)
            .movementType(MovementType.INBOUND)
            .toWarehouse(localWarehouse)
            .quantity(1)
            .confirmedByCommercial(true)
            .createdAt(ZonedDateTime.now());
        when(
            stockMovementRepository.findLatestByToWarehouseAndType(
                org.mockito.ArgumentMatchers.eq(localWarehouse.getId()),
                org.mockito.ArgumentMatchers.eq(MovementType.INBOUND),
                any(Pageable.class)
            )
        )
            .thenReturn(List.of(lastInbound));

        // Should not throw.
        stockMovementService.assertOutboundAllowed(localWarehouse);
    }

    @Test
    void assertOutboundAllowed_allowsWhenWarehouseNotLocal() {
        // Should be a no-op (no repository call, no exception).
        stockMovementService.assertOutboundAllowed(siteWarehouse);
        verify(stockMovementRepository, never()).findLatestByToWarehouseAndType(any(), any(), any());
    }

    @Test
    void assertOutboundAllowed_allowsWhenNoPriorInbound() {
        when(
            stockMovementRepository.findLatestByToWarehouseAndType(
                org.mockito.ArgumentMatchers.eq(localWarehouse.getId()),
                org.mockito.ArgumentMatchers.eq(MovementType.INBOUND),
                any(Pageable.class)
            )
        )
            .thenReturn(Collections.emptyList());

        // No prior INBOUND on this LOCAL warehouse: should not throw.
        stockMovementService.assertOutboundAllowed(localWarehouse);
    }

    @Test
    void save_blocksOutboundFromLocalWhenInboundUnconfirmed() {
        StockMovement persistedInput = new StockMovement()
            .movementType(MovementType.OUTBOUND)
            .fromWarehouse(localWarehouse)
            .quantity(1)
            .createdAt(ZonedDateTime.now());
        StockMovementDTO dto = new StockMovementDTO();
        dto.setMovementType(MovementType.OUTBOUND);
        WarehouseDTO whDto = new WarehouseDTO();
        whDto.setId(localWarehouse.getId());
        dto.setFromWarehouse(whDto);
        dto.setQuantity(1);
        dto.setCreatedAt(ZonedDateTime.now());

        StockMovement lastInbound = new StockMovement()
            .id(99L)
            .movementType(MovementType.INBOUND)
            .toWarehouse(localWarehouse)
            .quantity(1)
            .confirmedByCommercial(false)
            .createdAt(ZonedDateTime.now());

        when(stockMovementMapper.toEntity(any(StockMovementDTO.class))).thenReturn(persistedInput);
        lenient()
            .when(
                stockMovementRepository.findLatestByToWarehouseAndType(
                    org.mockito.ArgumentMatchers.eq(localWarehouse.getId()),
                    org.mockito.ArgumentMatchers.eq(MovementType.INBOUND),
                    any(Pageable.class)
                )
            )
            .thenReturn(List.of(lastInbound));

        assertThatThrownBy(() -> stockMovementService.save(dto)).isInstanceOf(IllegalStateException.class);
        verify(stockMovementRepository, times(0)).save(any());
    }
}
