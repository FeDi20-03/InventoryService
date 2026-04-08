package com.tunisales.inventory.service;

import com.tunisales.inventory.domain.*; // for static metamodels
import com.tunisales.inventory.domain.StockItem;
import com.tunisales.inventory.repository.StockItemRepository;
import com.tunisales.inventory.service.criteria.StockItemCriteria;
import com.tunisales.inventory.service.dto.StockItemDTO;
import com.tunisales.inventory.service.mapper.StockItemMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link StockItem} entities in the database.
 * The main input is a {@link StockItemCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link StockItemDTO} or a {@link Page} of {@link StockItemDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StockItemQueryService extends QueryService<StockItem> {

    private final Logger log = LoggerFactory.getLogger(StockItemQueryService.class);

    private final StockItemRepository stockItemRepository;

    private final StockItemMapper stockItemMapper;

    public StockItemQueryService(StockItemRepository stockItemRepository, StockItemMapper stockItemMapper) {
        this.stockItemRepository = stockItemRepository;
        this.stockItemMapper = stockItemMapper;
    }

    /**
     * Return a {@link List} of {@link StockItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<StockItemDTO> findByCriteria(StockItemCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<StockItem> specification = createSpecification(criteria);
        return stockItemMapper.toDto(stockItemRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link StockItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StockItemDTO> findByCriteria(StockItemCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<StockItem> specification = createSpecification(criteria);
        return stockItemRepository.findAll(specification, page).map(stockItemMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StockItemCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<StockItem> specification = createSpecification(criteria);
        return stockItemRepository.count(specification);
    }

    /**
     * Function to convert {@link StockItemCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<StockItem> createSpecification(StockItemCriteria criteria) {
        Specification<StockItem> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), StockItem_.id));
            }
            if (criteria.getTenantId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTenantId(), StockItem_.tenantId));
            }
            if (criteria.getProductId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getProductId(), StockItem_.productId));
            }
            if (criteria.getProductName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getProductName(), StockItem_.productName));
            }
            if (criteria.getImei() != null) {
                specification = specification.and(buildStringSpecification(criteria.getImei(), StockItem_.imei));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), StockItem_.status));
            }
            if (criteria.getIsDeleted() != null) {
                specification = specification.and(buildSpecification(criteria.getIsDeleted(), StockItem_.isDeleted));
            }
            if (criteria.getAcquiredAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAcquiredAt(), StockItem_.acquiredAt));
            }
            if (criteria.getUpdatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedAt(), StockItem_.updatedAt));
            }
            if (criteria.getStockMovementsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getStockMovementsId(),
                            root -> root.join(StockItem_.stockMovements, JoinType.LEFT).get(StockMovement_.id)
                        )
                    );
            }
            if (criteria.getWarehouseId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getWarehouseId(),
                            root -> root.join(StockItem_.warehouse, JoinType.LEFT).get(Warehouse_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
