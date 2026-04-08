package com.tunisales.inventory.service;

import com.tunisales.inventory.domain.*; // for static metamodels
import com.tunisales.inventory.domain.StockAudit;
import com.tunisales.inventory.repository.StockAuditRepository;
import com.tunisales.inventory.service.criteria.StockAuditCriteria;
import com.tunisales.inventory.service.dto.StockAuditDTO;
import com.tunisales.inventory.service.mapper.StockAuditMapper;
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
 * Service for executing complex queries for {@link StockAudit} entities in the database.
 * The main input is a {@link StockAuditCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link StockAuditDTO} or a {@link Page} of {@link StockAuditDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StockAuditQueryService extends QueryService<StockAudit> {

    private final Logger log = LoggerFactory.getLogger(StockAuditQueryService.class);

    private final StockAuditRepository stockAuditRepository;

    private final StockAuditMapper stockAuditMapper;

    public StockAuditQueryService(StockAuditRepository stockAuditRepository, StockAuditMapper stockAuditMapper) {
        this.stockAuditRepository = stockAuditRepository;
        this.stockAuditMapper = stockAuditMapper;
    }

    /**
     * Return a {@link List} of {@link StockAuditDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<StockAuditDTO> findByCriteria(StockAuditCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<StockAudit> specification = createSpecification(criteria);
        return stockAuditMapper.toDto(stockAuditRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link StockAuditDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StockAuditDTO> findByCriteria(StockAuditCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<StockAudit> specification = createSpecification(criteria);
        return stockAuditRepository.findAll(specification, page).map(stockAuditMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StockAuditCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<StockAudit> specification = createSpecification(criteria);
        return stockAuditRepository.count(specification);
    }

    /**
     * Function to convert {@link StockAuditCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<StockAudit> createSpecification(StockAuditCriteria criteria) {
        Specification<StockAudit> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), StockAudit_.id));
            }
            if (criteria.getTenantId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTenantId(), StockAudit_.tenantId));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), StockAudit_.status));
            }
            if (criteria.getTheoreticalCount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTheoreticalCount(), StockAudit_.theoreticalCount));
            }
            if (criteria.getPhysicalCount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPhysicalCount(), StockAudit_.physicalCount));
            }
            if (criteria.getDiscrepancyCount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDiscrepancyCount(), StockAudit_.discrepancyCount));
            }
            if (criteria.getNotes() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNotes(), StockAudit_.notes));
            }
            if (criteria.getAuditorLogin() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAuditorLogin(), StockAudit_.auditorLogin));
            }
            if (criteria.getStartedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartedAt(), StockAudit_.startedAt));
            }
            if (criteria.getClosedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getClosedAt(), StockAudit_.closedAt));
            }
            if (criteria.getAuditLinesId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getAuditLinesId(),
                            root -> root.join(StockAudit_.auditLines, JoinType.LEFT).get(StockAuditLine_.id)
                        )
                    );
            }
            if (criteria.getWarehouseId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getWarehouseId(),
                            root -> root.join(StockAudit_.warehouse, JoinType.LEFT).get(Warehouse_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
