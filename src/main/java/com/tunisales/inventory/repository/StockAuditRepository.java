package com.tunisales.inventory.repository;

import com.tunisales.inventory.domain.StockAudit;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StockAudit entity.
 */
@Repository
public interface StockAuditRepository extends JpaRepository<StockAudit, Long>, JpaSpecificationExecutor<StockAudit> {
    default Optional<StockAudit> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<StockAudit> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<StockAudit> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct stockAudit from StockAudit stockAudit left join fetch stockAudit.warehouse",
        countQuery = "select count(distinct stockAudit) from StockAudit stockAudit"
    )
    Page<StockAudit> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct stockAudit from StockAudit stockAudit left join fetch stockAudit.warehouse")
    List<StockAudit> findAllWithToOneRelationships();

    @Query("select stockAudit from StockAudit stockAudit left join fetch stockAudit.warehouse where stockAudit.id =:id")
    Optional<StockAudit> findOneWithToOneRelationships(@Param("id") Long id);
}
