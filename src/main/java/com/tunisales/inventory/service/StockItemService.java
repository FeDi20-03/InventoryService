package com.tunisales.inventory.service;

import com.tunisales.inventory.domain.StockItem;
import com.tunisales.inventory.repository.StockItemRepository;
import com.tunisales.inventory.service.dto.StockItemDTO;
import com.tunisales.inventory.service.mapper.StockItemMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public StockItemService(StockItemRepository stockItemRepository, StockItemMapper stockItemMapper) {
        this.stockItemRepository = stockItemRepository;
        this.stockItemMapper = stockItemMapper;
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
}
