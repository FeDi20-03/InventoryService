package com.tunisales.inventory.service;

import com.tunisales.inventory.domain.*; // for static metamodels
import com.tunisales.inventory.domain.StockMovement;
import com.tunisales.inventory.repository.StockMovementRepository;
import com.tunisales.inventory.service.criteria.StockMovementCriteria;
import com.tunisales.inventory.service.dto.StockMovementDTO;
import com.tunisales.inventory.service.mapper.StockMovementMapper;
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
 * Service for executing complex queries for {@link StockMovement} entities in the database.
 * The main input is a {@link StockMovementCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link StockMovementDTO} or a {@link Page} of {@link StockMovementDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StockMovementQueryService extends QueryService<StockMovement> {

    private final Logger log = LoggerFactory.getLogger(StockMovementQueryService.class);

    private final StockMovementRepository stockMovementRepository;

    private final StockMovementMapper stockMovementMapper;

    public StockMovementQueryService(StockMovementRepository stockMovementRepository, StockMovementMapper stockMovementMapper) {
        this.stockMovementRepository = stockMovementRepository;
        this.stockMovementMapper = stockMovementMapper;
    }

    /**
     * Return a {@link List} of {@link StockMovementDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<StockMovementDTO> findByCriteria(StockMovementCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<StockMovement> specification = createSpecification(criteria);
        return stockMovementMapper.toDto(stockMovementRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link StockMovementDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StockMovementDTO> findByCriteria(StockMovementCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<StockMovement> specification = createSpecification(criteria);
        return stockMovementRepository.findAll(specification, page).map(stockMovementMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StockMovementCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<StockMovement> specification = createSpecification(criteria);
        return stockMovementRepository.count(specification);
    }

    /**
     * Function to convert {@link StockMovementCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<StockMovement> createSpecification(StockMovementCriteria criteria) {
        Specification<StockMovement> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), StockMovement_.id));
            }
            if (criteria.getMovementType() != null) {
                specification = specification.and(buildSpecification(criteria.getMovementType(), StockMovement_.movementType));
            }
            if (criteria.getReason() != null) {
                specification = specification.and(buildStringSpecification(criteria.getReason(), StockMovement_.reason));
            }
            if (criteria.getReference() != null) {
                specification = specification.and(buildStringSpecification(criteria.getReference(), StockMovement_.reference));
            }
            if (criteria.getQuantity() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getQuantity(), StockMovement_.quantity));
            }
            if (criteria.getPerformedByLogin() != null) {
                specification =
                    specification.and(buildStringSpecification(criteria.getPerformedByLogin(), StockMovement_.performedByLogin));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), StockMovement_.createdAt));
            }
            if (criteria.getFromWarehouseId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getFromWarehouseId(),
                            root -> root.join(StockMovement_.fromWarehouse, JoinType.LEFT).get(Warehouse_.id)
                        )
                    );
            }
            if (criteria.getToWarehouseId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getToWarehouseId(),
                            root -> root.join(StockMovement_.toWarehouse, JoinType.LEFT).get(Warehouse_.id)
                        )
                    );
            }
            if (criteria.getStockItemId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getStockItemId(),
                            root -> root.join(StockMovement_.stockItem, JoinType.LEFT).get(StockItem_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
