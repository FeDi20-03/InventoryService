package com.tunisales.inventory.repository;

import com.tunisales.inventory.domain.StockMovement;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StockMovement entity.
 */
@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long>, JpaSpecificationExecutor<StockMovement> {
    default Optional<StockMovement> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<StockMovement> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<StockMovement> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct stockMovement from StockMovement stockMovement left join fetch stockMovement.fromWarehouse left join fetch stockMovement.toWarehouse left join fetch stockMovement.stockItem",
        countQuery = "select count(distinct stockMovement) from StockMovement stockMovement"
    )
    Page<StockMovement> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct stockMovement from StockMovement stockMovement left join fetch stockMovement.fromWarehouse left join fetch stockMovement.toWarehouse left join fetch stockMovement.stockItem"
    )
    List<StockMovement> findAllWithToOneRelationships();

    @Query(
        "select stockMovement from StockMovement stockMovement left join fetch stockMovement.fromWarehouse left join fetch stockMovement.toWarehouse left join fetch stockMovement.stockItem where stockMovement.id =:id"
    )
    Optional<StockMovement> findOneWithToOneRelationships(@Param("id") Long id);
}
