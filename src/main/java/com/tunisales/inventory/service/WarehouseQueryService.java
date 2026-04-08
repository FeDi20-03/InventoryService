package com.tunisales.inventory.service;

import com.tunisales.inventory.domain.*; // for static metamodels
import com.tunisales.inventory.domain.Warehouse;
import com.tunisales.inventory.repository.WarehouseRepository;
import com.tunisales.inventory.service.criteria.WarehouseCriteria;
import com.tunisales.inventory.service.dto.WarehouseDTO;
import com.tunisales.inventory.service.mapper.WarehouseMapper;
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
 * Service for executing complex queries for {@link Warehouse} entities in the database.
 * The main input is a {@link WarehouseCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link WarehouseDTO} or a {@link Page} of {@link WarehouseDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class WarehouseQueryService extends QueryService<Warehouse> {

    private final Logger log = LoggerFactory.getLogger(WarehouseQueryService.class);

    private final WarehouseRepository warehouseRepository;

    private final WarehouseMapper warehouseMapper;

    public WarehouseQueryService(WarehouseRepository warehouseRepository, WarehouseMapper warehouseMapper) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseMapper = warehouseMapper;
    }

    /**
     * Return a {@link List} of {@link WarehouseDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<WarehouseDTO> findByCriteria(WarehouseCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Warehouse> specification = createSpecification(criteria);
        return warehouseMapper.toDto(warehouseRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link WarehouseDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<WarehouseDTO> findByCriteria(WarehouseCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Warehouse> specification = createSpecification(criteria);
        return warehouseRepository.findAll(specification, page).map(warehouseMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(WarehouseCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Warehouse> specification = createSpecification(criteria);
        return warehouseRepository.count(specification);
    }

    /**
     * Function to convert {@link WarehouseCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Warehouse> createSpecification(WarehouseCriteria criteria) {
        Specification<Warehouse> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Warehouse_.id));
            }
            if (criteria.getTenantId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTenantId(), Warehouse_.tenantId));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Warehouse_.name));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildSpecification(criteria.getType(), Warehouse_.type));
            }
            if (criteria.getAddress() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddress(), Warehouse_.address));
            }
            if (criteria.getCity() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCity(), Warehouse_.city));
            }
            if (criteria.getMinThreshold() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getMinThreshold(), Warehouse_.minThreshold));
            }
            if (criteria.getIsActive() != null) {
                specification = specification.and(buildSpecification(criteria.getIsActive(), Warehouse_.isActive));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), Warehouse_.createdAt));
            }
            if (criteria.getUpdatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedAt(), Warehouse_.updatedAt));
            }
            if (criteria.getStockItemsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getStockItemsId(),
                            root -> root.join(Warehouse_.stockItems, JoinType.LEFT).get(StockItem_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
