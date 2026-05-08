package com.tunisales.inventory.service;

import com.tunisales.inventory.domain.StockMovement;
import com.tunisales.inventory.domain.Warehouse;
import com.tunisales.inventory.domain.enumeration.MovementType;
import com.tunisales.inventory.domain.enumeration.WarehouseType;
import com.tunisales.inventory.repository.StockMovementRepository;
import com.tunisales.inventory.service.dto.StockMovementDTO;
import com.tunisales.inventory.service.mapper.StockMovementMapper;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link StockMovement}.
 *
 * <p>Étape 1.4 introduces the {@code confirmedByCommercial} workflow used to
 * validate the alimentation of a commercial's vehicle (a LOCAL warehouse):</p>
 * <ul>
 *   <li>{@link #validateByCommercial(Long, String)} flips the flag on an INBOUND
 *       movement landing on a LOCAL warehouse.</li>
 *   <li>{@link #assertOutboundAllowed(Warehouse)} blocks any OUTBOUND/TRANSFER
 *       from a LOCAL warehouse until the most recent INBOUND has been
 *       validated.</li>
 * </ul>
 */
@Service
@Transactional
public class StockMovementService {

    private final Logger log = LoggerFactory.getLogger(StockMovementService.class);

    private final StockMovementRepository stockMovementRepository;

    private final StockMovementMapper stockMovementMapper;

    private final WarehouseStockThresholdService thresholdService;

    public StockMovementService(
        StockMovementRepository stockMovementRepository,
        StockMovementMapper stockMovementMapper,
        WarehouseStockThresholdService thresholdService
    ) {
        this.stockMovementRepository = stockMovementRepository;
        this.stockMovementMapper = stockMovementMapper;
        this.thresholdService = thresholdService;
    }

    public StockMovementDTO save(StockMovementDTO stockMovementDTO) {
        log.debug("Request to save StockMovement : {}", stockMovementDTO);
        StockMovement stockMovement = stockMovementMapper.toEntity(stockMovementDTO);

        // Block OUTBOUND/TRANSFER from a LOCAL warehouse if its last INBOUND
        // has not been validated by a commercial.
        if (stockMovement.getFromWarehouse() != null && requiresCommercialValidation(stockMovement.getMovementType())) {
            assertOutboundAllowed(stockMovement.getFromWarehouse());
        }

        stockMovement = stockMovementRepository.save(stockMovement);

        // 1.6 — STOCK_LOW notification if the source warehouse fell below its
        // minThreshold after this movement.
        if (stockMovement.getFromWarehouse() != null) {
            thresholdService.checkAndNotify(stockMovement.getFromWarehouse());
        }

        return stockMovementMapper.toDto(stockMovement);
    }

    public StockMovementDTO update(StockMovementDTO stockMovementDTO) {
        log.debug("Request to update StockMovement : {}", stockMovementDTO);
        StockMovement stockMovement = stockMovementMapper.toEntity(stockMovementDTO);
        stockMovement = stockMovementRepository.save(stockMovement);
        return stockMovementMapper.toDto(stockMovement);
    }

    public Optional<StockMovementDTO> partialUpdate(StockMovementDTO stockMovementDTO) {
        log.debug("Request to partially update StockMovement : {}", stockMovementDTO);

        return stockMovementRepository
            .findById(stockMovementDTO.getId())
            .map(existingStockMovement -> {
                stockMovementMapper.partialUpdate(existingStockMovement, stockMovementDTO);

                return existingStockMovement;
            })
            .map(stockMovementRepository::save)
            .map(stockMovementMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<StockMovementDTO> findAll(Pageable pageable) {
        log.debug("Request to get all StockMovements");
        return stockMovementRepository.findAll(pageable).map(stockMovementMapper::toDto);
    }

    public Page<StockMovementDTO> findAllWithEagerRelationships(Pageable pageable) {
        return stockMovementRepository.findAllWithEagerRelationships(pageable).map(stockMovementMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<StockMovementDTO> findOne(Long id) {
        log.debug("Request to get StockMovement : {}", id);
        return stockMovementRepository.findOneWithEagerRelationships(id).map(stockMovementMapper::toDto);
    }

    public void delete(Long id) {
        log.debug("Request to delete StockMovement : {}", id);
        stockMovementRepository.deleteById(id);
    }

    // ===================================================================
    // 1.4 — Commercial validation of vehicle alimentation
    // ===================================================================

    /**
     * Confirm an INBOUND movement landing on a LOCAL warehouse.
     *
     * @throws NoSuchElementException if no movement exists with the given id.
     * @throws IllegalStateException  if the movement is not INBOUND or the target
     *                                warehouse is not LOCAL.
     */
    public StockMovementDTO validateByCommercial(Long movementId, String commercialLogin) {
        log.debug("Request to validate movement {} by commercial {}", movementId, commercialLogin);
        StockMovement movement = stockMovementRepository
            .findOneWithEagerRelationships(movementId)
            .orElseThrow(() -> new NoSuchElementException("StockMovement " + movementId + " not found"));

        if (movement.getMovementType() != MovementType.INBOUND) {
            throw new IllegalStateException("Only INBOUND movements can be validated by a commercial");
        }
        Warehouse target = movement.getToWarehouse();
        if (target == null || target.getType() != WarehouseType.LOCAL) {
            throw new IllegalStateException("Only INBOUND movements onto a LOCAL warehouse can be validated by a commercial");
        }

        movement.setConfirmedByCommercial(true);
        if (commercialLogin != null) {
            movement.setPerformedByLogin(commercialLogin);
        }
        movement = stockMovementRepository.save(movement);
        return stockMovementMapper.toDto(movement);
    }

    /**
     * Throws an {@link IllegalStateException} if {@code source} is a LOCAL
     * warehouse whose most recent INBOUND movement has not yet been confirmed
     * by a commercial. Returns silently otherwise.
     *
     * <p>The contract mirrors the documented 409 behaviour: callers translate
     * the exception into a {@code 409 Conflict} HTTP response.</p>
     */
    @Transactional(readOnly = true)
    public void assertOutboundAllowed(Warehouse source) {
        if (source == null || source.getType() != WarehouseType.LOCAL) {
            return;
        }
        List<StockMovement> latest = stockMovementRepository.findLatestByToWarehouseAndType(
            source.getId(),
            MovementType.INBOUND,
            PageRequest.of(0, 1)
        );
        if (latest.isEmpty()) {
            // No prior INBOUND on this LOCAL warehouse; nothing to validate.
            return;
        }
        StockMovement lastInbound = latest.get(0);
        if (!Boolean.TRUE.equals(lastInbound.getConfirmedByCommercial())) {
            throw new IllegalStateException(
                "Outbound from LOCAL warehouse " + source.getId() + " is blocked: latest INBOUND has not been confirmed by a commercial"
            );
        }
    }

    private static boolean requiresCommercialValidation(MovementType type) {
        return type == MovementType.OUTBOUND || type == MovementType.TRANSFER || type == MovementType.SWAP_OUT;
    }
}
