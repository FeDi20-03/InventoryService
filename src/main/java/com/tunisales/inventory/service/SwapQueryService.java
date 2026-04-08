package com.tunisales.inventory.service;

import com.tunisales.inventory.domain.*; // for static metamodels
import com.tunisales.inventory.domain.Swap;
import com.tunisales.inventory.repository.SwapRepository;
import com.tunisales.inventory.service.criteria.SwapCriteria;
import com.tunisales.inventory.service.dto.SwapDTO;
import com.tunisales.inventory.service.mapper.SwapMapper;
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
 * Service for executing complex queries for {@link Swap} entities in the database.
 * The main input is a {@link SwapCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SwapDTO} or a {@link Page} of {@link SwapDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SwapQueryService extends QueryService<Swap> {

    private final Logger log = LoggerFactory.getLogger(SwapQueryService.class);

    private final SwapRepository swapRepository;

    private final SwapMapper swapMapper;

    public SwapQueryService(SwapRepository swapRepository, SwapMapper swapMapper) {
        this.swapRepository = swapRepository;
        this.swapMapper = swapMapper;
    }

    /**
     * Return a {@link List} of {@link SwapDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SwapDTO> findByCriteria(SwapCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Swap> specification = createSpecification(criteria);
        return swapMapper.toDto(swapRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link SwapDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SwapDTO> findByCriteria(SwapCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Swap> specification = createSpecification(criteria);
        return swapRepository.findAll(specification, page).map(swapMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SwapCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Swap> specification = createSpecification(criteria);
        return swapRepository.count(specification);
    }

    /**
     * Function to convert {@link SwapCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Swap> createSpecification(SwapCriteria criteria) {
        Specification<Swap> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Swap_.id));
            }
            if (criteria.getTenantId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTenantId(), Swap_.tenantId));
            }
            if (criteria.getClientId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getClientId(), Swap_.clientId));
            }
            if (criteria.getClientName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getClientName(), Swap_.clientName));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Swap_.status));
            }
            if (criteria.getReason() != null) {
                specification = specification.and(buildStringSpecification(criteria.getReason(), Swap_.reason));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), Swap_.createdAt));
            }
            if (criteria.getResolvedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getResolvedAt(), Swap_.resolvedAt));
            }
            if (criteria.getUpdatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedAt(), Swap_.updatedAt));
            }
            if (criteria.getOutgoingItemId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getOutgoingItemId(),
                            root -> root.join(Swap_.outgoingItem, JoinType.LEFT).get(StockItem_.id)
                        )
                    );
            }
            if (criteria.getIncomingItemId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getIncomingItemId(),
                            root -> root.join(Swap_.incomingItem, JoinType.LEFT).get(StockItem_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
