package com.tunisales.inventory.service;

import com.tunisales.inventory.domain.StockAudit;
import com.tunisales.inventory.repository.StockAuditRepository;
import com.tunisales.inventory.service.dto.StockAuditDTO;
import com.tunisales.inventory.service.mapper.StockAuditMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link StockAudit}.
 */
@Service
@Transactional
public class StockAuditService {

    private final Logger log = LoggerFactory.getLogger(StockAuditService.class);

    private final StockAuditRepository stockAuditRepository;

    private final StockAuditMapper stockAuditMapper;

    public StockAuditService(StockAuditRepository stockAuditRepository, StockAuditMapper stockAuditMapper) {
        this.stockAuditRepository = stockAuditRepository;
        this.stockAuditMapper = stockAuditMapper;
    }

    /**
     * Save a stockAudit.
     *
     * @param stockAuditDTO the entity to save.
     * @return the persisted entity.
     */
    public StockAuditDTO save(StockAuditDTO stockAuditDTO) {
        log.debug("Request to save StockAudit : {}", stockAuditDTO);
        StockAudit stockAudit = stockAuditMapper.toEntity(stockAuditDTO);
        stockAudit = stockAuditRepository.save(stockAudit);
        return stockAuditMapper.toDto(stockAudit);
    }

    /**
     * Update a stockAudit.
     *
     * @param stockAuditDTO the entity to save.
     * @return the persisted entity.
     */
    public StockAuditDTO update(StockAuditDTO stockAuditDTO) {
        log.debug("Request to update StockAudit : {}", stockAuditDTO);
        StockAudit stockAudit = stockAuditMapper.toEntity(stockAuditDTO);
        stockAudit = stockAuditRepository.save(stockAudit);
        return stockAuditMapper.toDto(stockAudit);
    }

    /**
     * Partially update a stockAudit.
     *
     * @param stockAuditDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<StockAuditDTO> partialUpdate(StockAuditDTO stockAuditDTO) {
        log.debug("Request to partially update StockAudit : {}", stockAuditDTO);

        return stockAuditRepository
            .findById(stockAuditDTO.getId())
            .map(existingStockAudit -> {
                stockAuditMapper.partialUpdate(existingStockAudit, stockAuditDTO);

                return existingStockAudit;
            })
            .map(stockAuditRepository::save)
            .map(stockAuditMapper::toDto);
    }

    /**
     * Get all the stockAudits.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<StockAuditDTO> findAll(Pageable pageable) {
        log.debug("Request to get all StockAudits");
        return stockAuditRepository.findAll(pageable).map(stockAuditMapper::toDto);
    }

    /**
     * Get all the stockAudits with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<StockAuditDTO> findAllWithEagerRelationships(Pageable pageable) {
        return stockAuditRepository.findAllWithEagerRelationships(pageable).map(stockAuditMapper::toDto);
    }

    /**
     * Get one stockAudit by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StockAuditDTO> findOne(Long id) {
        log.debug("Request to get StockAudit : {}", id);
        return stockAuditRepository.findOneWithEagerRelationships(id).map(stockAuditMapper::toDto);
    }

    /**
     * Delete the stockAudit by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete StockAudit : {}", id);
        stockAuditRepository.deleteById(id);
    }
}
