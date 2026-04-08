package com.tunisales.inventory.service;

import com.tunisales.inventory.domain.StockMovement;
import com.tunisales.inventory.repository.StockMovementRepository;
import com.tunisales.inventory.service.dto.StockMovementDTO;
import com.tunisales.inventory.service.mapper.StockMovementMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link StockMovement}.
 */
@Service
@Transactional
public class StockMovementService {

    private final Logger log = LoggerFactory.getLogger(StockMovementService.class);

    private final StockMovementRepository stockMovementRepository;

    private final StockMovementMapper stockMovementMapper;

    public StockMovementService(StockMovementRepository stockMovementRepository, StockMovementMapper stockMovementMapper) {
        this.stockMovementRepository = stockMovementRepository;
        this.stockMovementMapper = stockMovementMapper;
    }

    /**
     * Save a stockMovement.
     *
     * @param stockMovementDTO the entity to save.
     * @return the persisted entity.
     */
    public StockMovementDTO save(StockMovementDTO stockMovementDTO) {
        log.debug("Request to save StockMovement : {}", stockMovementDTO);
        StockMovement stockMovement = stockMovementMapper.toEntity(stockMovementDTO);
        stockMovement = stockMovementRepository.save(stockMovement);
        return stockMovementMapper.toDto(stockMovement);
    }

    /**
     * Update a stockMovement.
     *
     * @param stockMovementDTO the entity to save.
     * @return the persisted entity.
     */
    public StockMovementDTO update(StockMovementDTO stockMovementDTO) {
        log.debug("Request to update StockMovement : {}", stockMovementDTO);
        StockMovement stockMovement = stockMovementMapper.toEntity(stockMovementDTO);
        stockMovement = stockMovementRepository.save(stockMovement);
        return stockMovementMapper.toDto(stockMovement);
    }

    /**
     * Partially update a stockMovement.
     *
     * @param stockMovementDTO the entity to update partially.
     * @return the persisted entity.
     */
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

    /**
     * Get all the stockMovements.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<StockMovementDTO> findAll(Pageable pageable) {
        log.debug("Request to get all StockMovements");
        return stockMovementRepository.findAll(pageable).map(stockMovementMapper::toDto);
    }

    /**
     * Get all the stockMovements with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<StockMovementDTO> findAllWithEagerRelationships(Pageable pageable) {
        return stockMovementRepository.findAllWithEagerRelationships(pageable).map(stockMovementMapper::toDto);
    }

    /**
     * Get one stockMovement by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StockMovementDTO> findOne(Long id) {
        log.debug("Request to get StockMovement : {}", id);
        return stockMovementRepository.findOneWithEagerRelationships(id).map(stockMovementMapper::toDto);
    }

    /**
     * Delete the stockMovement by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete StockMovement : {}", id);
        stockMovementRepository.deleteById(id);
    }
}
