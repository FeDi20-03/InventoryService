package com.tunisales.inventory.service;

import com.tunisales.inventory.client.PlatformNotificationClient;
import com.tunisales.inventory.domain.StockItem;
import com.tunisales.inventory.domain.StockItemStatusMachine;
import com.tunisales.inventory.domain.StockMovement;
import com.tunisales.inventory.domain.Warehouse;
import com.tunisales.inventory.domain.enumeration.MovementType;
import com.tunisales.inventory.domain.enumeration.StockItemStatus;
import com.tunisales.inventory.domain.enumeration.WarehouseType;
import com.tunisales.inventory.repository.StockItemRepository;
import com.tunisales.inventory.repository.StockMovementRepository;
import com.tunisales.inventory.repository.WarehouseRepository;
import com.tunisales.inventory.service.dto.NotificationPayloadDTO;
import com.tunisales.inventory.service.dto.StockItemDTO;
import com.tunisales.inventory.service.dto.StockItemScanDTO;
import com.tunisales.inventory.service.mapper.StockItemMapper;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link StockItem}.
 */
@Service
@Transactional
public class StockItemService {

    private final Logger log = LoggerFactory.getLogger(StockItemService.class);

    private final StockItemRepository stockItemRepository;

    private final StockItemMapper stockItemMapper;

    private final WarehouseRepository warehouseRepository;

    private final StockMovementRepository stockMovementRepository;

    private final PlatformNotificationClient notificationClient;

    @Value("${tunisales.inventory.default-site-warehouse:MAIN_SITE}")
    private String defaultSiteWarehouseName;

    public StockItemService(
        StockItemRepository stockItemRepository,
        StockItemMapper stockItemMapper,
        WarehouseRepository warehouseRepository,
        StockMovementRepository stockMovementRepository,
        PlatformNotificationClient notificationClient
    ) {
        this.stockItemRepository = stockItemRepository;
        this.stockItemMapper = stockItemMapper;
        this.warehouseRepository = warehouseRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.notificationClient = notificationClient;
    }

    /**
     * Save a stockItem.
     *
     * @param stockItemDTO the entity to save.
     * @return the persisted entity.
     */
    public StockItemDTO save(StockItemDTO stockItemDTO) {
        log.debug("Request to save StockItem : {}", stockItemDTO);
        StockItem stockItem = stockItemMapper.toEntity(stockItemDTO);
        stockItem = stockItemRepository.save(stockItem);
        return stockItemMapper.toDto(stockItem);
    }

    /**
     * Update a stockItem.
     *
     * @param stockItemDTO the entity to save.
     * @return the persisted entity.
     */
    public StockItemDTO update(StockItemDTO stockItemDTO) {
        log.debug("Request to update StockItem : {}", stockItemDTO);
        StockItem stockItem = stockItemMapper.toEntity(stockItemDTO);
        stockItem = stockItemRepository.save(stockItem);
        return stockItemMapper.toDto(stockItem);
    }

    /**
     * Partially update a stockItem.
     *
     * @param stockItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<StockItemDTO> partialUpdate(StockItemDTO stockItemDTO) {
        log.debug("Request to partially update StockItem : {}", stockItemDTO);

        return stockItemRepository
            .findById(stockItemDTO.getId())
            .map(existingStockItem -> {
                stockItemMapper.partialUpdate(existingStockItem, stockItemDTO);

                return existingStockItem;
            })
            .map(stockItemRepository::save)
            .map(stockItemMapper::toDto);
    }

    /**
     * Get all the stockItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<StockItemDTO> findAll(Pageable pageable) {
        log.debug("Request to get all StockItems");
        return stockItemRepository.findAll(pageable).map(stockItemMapper::toDto);
    }

    /**
     * Get all the stockItems with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<StockItemDTO> findAllWithEagerRelationships(Pageable pageable) {
        return stockItemRepository.findAllWithEagerRelationships(pageable).map(stockItemMapper::toDto);
    }

    /**
     * Get one stockItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StockItemDTO> findOne(Long id) {
        log.debug("Request to get StockItem : {}", id);
        return stockItemRepository.findOneWithEagerRelationships(id).map(stockItemMapper::toDto);
    }

    /**
     * Delete the stockItem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete StockItem : {}", id);
        stockItemRepository.deleteById(id);
    }

    /**
     * Look up a {@link StockItem} by its IMEI and return an enriched read model
     * suitable for a barcode-scanner workflow.
     *
     * <p>The caller is expected to have validated the IMEI syntactically
     * (length, Luhn) before calling this method.</p>
     *
     * @param imei the IMEI to look up; must be exactly 15 digits.
     * @return the enriched scan DTO if a stock item with this IMEI exists.
     */
    @Transactional(readOnly = true)
    public Optional<StockItemScanDTO> scanByImei(String imei) {
        log.debug("Request to scan StockItem by IMEI : {}", imei);
        return stockItemRepository.findOneByImeiWithEagerRelationships(imei).map(this::toScanDto);
    }

    private StockItemScanDTO toScanDto(StockItem item) {
        StockItemScanDTO dto = new StockItemScanDTO();
        dto.setId(item.getId());
        dto.setTenantId(item.getTenantId());
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setImei(item.getImei());
        dto.setStatus(item.getStatus());

        Warehouse warehouse = item.getWarehouse();
        if (warehouse != null) {
            dto.setWarehouseId(warehouse.getId());
            dto.setWarehouseName(warehouse.getName());
            dto.setWarehouseType(warehouse.getType());
        }

        dto.setLastMovementAt(
            item
                .getStockMovements()
                .stream()
                .map(StockMovement::getCreatedAt)
                .filter(java.util.Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse((ZonedDateTime) null)
        );
        return dto;
    }

    // ===================================================================
    // 1.2 — Status transitions
    // ===================================================================

    /**
     * Declare a {@code MISSING} item as definitively {@code LOST}.
     *
     * <p>Only allowed when the current status is {@link StockItemStatus#MISSING}
     * (cf. {@link StockItemStatusMachine}). The {@code declaredByLogin} and
     * {@code reason} are appended to {@code productName}-adjacent metadata via
     * a synthetic {@link StockMovement} of type {@link MovementType#INVENTORY_ADJUSTMENT}
     * to keep an audit trail.</p>
     *
     * @throws NoSuchElementException if no item exists with the given id.
     * @throws IllegalStateException  if the item is not currently {@code MISSING}.
     */
    public StockItemDTO declareLost(Long id, String declaredByLogin, String reason) {
        log.debug("Request to declare StockItem {} as LOST by {} (reason: {})", id, declaredByLogin, reason);
        StockItem item = stockItemRepository
            .findOneWithEagerRelationships(id)
            .orElseThrow(() -> new NoSuchElementException("StockItem " + id + " not found"));

        StockItemStatusMachine.assertCanTransition(item.getStatus(), StockItemStatus.LOST);

        item.setStatus(StockItemStatus.LOST);
        item.setUpdatedAt(ZonedDateTime.now());

        StockMovement movement = new StockMovement()
            .movementType(MovementType.INVENTORY_ADJUSTMENT)
            .reason(safeReason("Declared LOST", reason))
            .reference("LOST/" + (declaredByLogin != null ? declaredByLogin : "unknown"))
            .quantity(1)
            .performedByLogin(declaredByLogin)
            .createdAt(ZonedDateTime.now())
            .stockItem(item)
            .fromWarehouse(item.getWarehouse());
        stockMovementRepository.save(movement);

        item = stockItemRepository.save(item);
        return stockItemMapper.toDto(item);
    }

    /**
     * Mark a {@code DEFECTIVE} item as repaired.
     *
     * <p>Sets the status back to {@link StockItemStatus#AVAILABLE} and physically
     * moves it to the configured default site warehouse
     * ({@code tunisales.inventory.default-site-warehouse}, by name; falls back to
     * the first active warehouse of type {@link WarehouseType#SITE} if no
     * warehouse with the configured name is found).</p>
     *
     * @throws NoSuchElementException if no item exists with the given id, or no
     *                                site warehouse can be resolved.
     * @throws IllegalStateException  if the item is not currently {@code DEFECTIVE}.
     */
    public StockItemDTO markRepaired(Long id) {
        log.debug("Request to mark StockItem {} as repaired", id);
        StockItem item = stockItemRepository
            .findOneWithEagerRelationships(id)
            .orElseThrow(() -> new NoSuchElementException("StockItem " + id + " not found"));

        StockItemStatusMachine.assertCanTransition(item.getStatus(), StockItemStatus.AVAILABLE);

        Warehouse target = resolveDefaultSiteWarehouse();
        Warehouse from = item.getWarehouse();

        item.setStatus(StockItemStatus.AVAILABLE);
        item.setWarehouse(target);
        item.setUpdatedAt(ZonedDateTime.now());

        StockMovement movement = new StockMovement()
            .movementType(MovementType.TRANSFER)
            .reason("Repaired - returned to main site")
            .reference("REPAIRED/" + id)
            .quantity(1)
            .createdAt(ZonedDateTime.now())
            .stockItem(item)
            .fromWarehouse(from)
            .toWarehouse(target);
        stockMovementRepository.save(movement);

        item = stockItemRepository.save(item);
        return stockItemMapper.toDto(item);
    }

    /**
     * Resolve the default "main site" warehouse used by {@link #markRepaired(Long)}.
     *
     * <p>Resolution order:
     * <ol>
     *   <li>Active warehouse whose {@code name} equals the configured value;</li>
     *   <li>First active warehouse of type {@link WarehouseType#SITE}.</li>
     * </ol>
     * </p>
     */
    private Warehouse resolveDefaultSiteWarehouse() {
        return warehouseRepository
            .findFirstByNameAndIsActiveTrue(defaultSiteWarehouseName)
            .or(() -> warehouseRepository.findFirstByTypeAndIsActiveTrueOrderByIdAsc(WarehouseType.SITE))
            .orElseThrow(() ->
                new NoSuchElementException("No active site warehouse found (looked up by name '" + defaultSiteWarehouseName + "')")
            );
    }

    /**
     * Mark an item as MISSING. Emits a STOCK_MISSING notification (1.6).
     */
    public StockItemDTO markMissing(Long id, String declaredByLogin, String reason) {
        log.debug("Request to mark StockItem {} as MISSING by {} (reason: {})", id, declaredByLogin, reason);
        StockItem item = stockItemRepository
            .findOneWithEagerRelationships(id)
            .orElseThrow(() -> new NoSuchElementException("StockItem " + id + " not found"));

        StockItemStatusMachine.assertCanTransition(item.getStatus(), StockItemStatus.MISSING);
        item.setStatus(StockItemStatus.MISSING);
        item.setUpdatedAt(ZonedDateTime.now());

        StockMovement movement = new StockMovement()
            .movementType(MovementType.INVENTORY_ADJUSTMENT)
            .reason(safeReason("Marked MISSING", reason))
            .reference("MISSING/" + (declaredByLogin != null ? declaredByLogin : "unknown"))
            .quantity(1)
            .performedByLogin(declaredByLogin)
            .createdAt(ZonedDateTime.now())
            .stockItem(item)
            .fromWarehouse(item.getWarehouse());
        stockMovementRepository.save(movement);

        item = stockItemRepository.save(item);

        try {
            notificationClient.publish(
                new NotificationPayloadDTO(
                    "*ADMIN_COMMERCIAL*",
                    "STOCK_MISSING",
                    "Stock item missing",
                    "IMEI " + item.getImei() + " is now MISSING",
                    "{\"stockItemId\":" + item.getId() + ",\"imei\":\"" + item.getImei() + "\"}"
                )
            );
        } catch (Exception ex) {
            log.warn("Failed to publish STOCK_MISSING notification for item {}: {}", id, ex.getMessage());
        }

        return stockItemMapper.toDto(item);
    }

    /**
     * Trigger a rework request to the GPF system. The actual HTTP call is
     * delegated to {@code GpfReworkClient} via the rework service. See 1.7.
     */
    public Long sendToRework(Long id, String reason) {
        // Implementation lives in ReworkRequestService to keep the rework
        // bookkeeping isolated from the StockItem aggregate.
        throw new UnsupportedOperationException("Use ReworkRequestService.sendToRework instead");
    }

    private static String safeReason(String prefix, String reason) {
        if (reason == null || reason.isBlank()) {
            return prefix;
        }
        String combined = prefix + ": " + reason;
        return combined.length() > 500 ? combined.substring(0, 500) : combined;
    }
}
