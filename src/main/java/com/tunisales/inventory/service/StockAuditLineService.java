package com.tunisales.inventory.service;

import com.tunisales.inventory.domain.StockAuditLine;
import com.tunisales.inventory.repository.StockAuditLineRepository;
import com.tunisales.inventory.service.dto.StockAuditLineDTO;
import com.tunisales.inventory.service.mapper.StockAuditLineMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link StockAuditLine}.
 */
@Service
@Transactional
public class StockAuditLineService {

    private final Logger log = LoggerFactory.getLogger(StockAuditLineService.class);

    private final StockAuditLineRepository stockAuditLineRepository;

    private final StockAuditLineMapper stockAuditLineMapper;

    public StockAuditLineService(StockAuditLineRepository stockAuditLineRepository, StockAuditLineMapper stockAuditLineMapper) {
        this.stockAuditLineRepository = stockAuditLineRepository;
        this.stockAuditLineMapper = stockAuditLineMapper;
    }

    /**
     * Save a stockAuditLine.
     *
     * @param stockAuditLineDTO the entity to save.
     * @return the persisted entity.
     */
    public StockAuditLineDTO save(StockAuditLineDTO stockAuditLineDTO) {
        log.debug("Request to save StockAuditLine : {}", stockAuditLineDTO);
        StockAuditLine stockAuditLine = stockAuditLineMapper.toEntity(stockAuditLineDTO);
        stockAuditLine = stockAuditLineRepository.save(stockAuditLine);
        return stockAuditLineMapper.toDto(stockAuditLine);
    }

    /**
     * Update a stockAuditLine.
     *
     * @param stockAuditLineDTO the entity to save.
     * @return the persisted entity.
     */
    public StockAuditLineDTO update(StockAuditLineDTO stockAuditLineDTO) {
        log.debug("Request to update StockAuditLine : {}", stockAuditLineDTO);
        StockAuditLine stockAuditLine = stockAuditLineMapper.toEntity(stockAuditLineDTO);
        stockAuditLine = stockAuditLineRepository.save(stockAuditLine);
        return stockAuditLineMapper.toDto(stockAuditLine);
    }

    /**
     * Partially update a stockAuditLine.
     *
     * @param stockAuditLineDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<StockAuditLineDTO> partialUpdate(StockAuditLineDTO stockAuditLineDTO) {
        log.debug("Request to partially update StockAuditLine : {}", stockAuditLineDTO);

        return stockAuditLineRepository
            .findById(stockAuditLineDTO.getId())
            .map(existingStockAuditLine -> {
                stockAuditLineMapper.partialUpdate(existingStockAuditLine, stockAuditLineDTO);

                return existingStockAuditLine;
            })
            .map(stockAuditLineRepository::save)
            .map(stockAuditLineMapper::toDto);
    }

    /**
     * Get all the stockAuditLines.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<StockAuditLineDTO> findAll(Pageable pageable) {
        log.debug("Request to get all StockAuditLines");
        return stockAuditLineRepository.findAll(pageable).map(stockAuditLineMapper::toDto);
    }

    /**
     * Get all the stockAuditLines with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<StockAuditLineDTO> findAllWithEagerRelationships(Pageable pageable) {
        return stockAuditLineRepository.findAllWithEagerRelationships(pageable).map(stockAuditLineMapper::toDto);
    }

    /**
     * Get one stockAuditLine by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StockAuditLineDTO> findOne(Long id) {
        log.debug("Request to get StockAuditLine : {}", id);
        return stockAuditLineRepository.findOneWithEagerRelationships(id).map(stockAuditLineMapper::toDto);
    }

    /**
     * Delete the stockAuditLine by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete StockAuditLine : {}", id);
        stockAuditLineRepository.deleteById(id);
    }
}
