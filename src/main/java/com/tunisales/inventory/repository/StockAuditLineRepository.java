package com.tunisales.inventory.repository;

import com.tunisales.inventory.domain.StockAuditLine;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StockAuditLine entity.
 */
@Repository
public interface StockAuditLineRepository extends JpaRepository<StockAuditLine, Long> {
    default Optional<StockAuditLine> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<StockAuditLine> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<StockAuditLine> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct stockAuditLine from StockAuditLine stockAuditLine left join fetch stockAuditLine.stockItem",
        countQuery = "select count(distinct stockAuditLine) from StockAuditLine stockAuditLine"
    )
    Page<StockAuditLine> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct stockAuditLine from StockAuditLine stockAuditLine left join fetch stockAuditLine.stockItem")
    List<StockAuditLine> findAllWithToOneRelationships();

    @Query("select stockAuditLine from StockAuditLine stockAuditLine left join fetch stockAuditLine.stockItem where stockAuditLine.id =:id")
    Optional<StockAuditLine> findOneWithToOneRelationships(@Param("id") Long id);
}
