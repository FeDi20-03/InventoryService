package com.tunisales.inventory.repository;

import com.tunisales.inventory.domain.StockItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StockItem entity.
 */
@Repository
public interface StockItemRepository extends JpaRepository<StockItem, Long>, JpaSpecificationExecutor<StockItem> {
    default Optional<StockItem> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<StockItem> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<StockItem> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct stockItem from StockItem stockItem left join fetch stockItem.warehouse",
        countQuery = "select count(distinct stockItem) from StockItem stockItem"
    )
    Page<StockItem> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct stockItem from StockItem stockItem left join fetch stockItem.warehouse")
    List<StockItem> findAllWithToOneRelationships();

    @Query("select stockItem from StockItem stockItem left join fetch stockItem.warehouse where stockItem.id =:id")
    Optional<StockItem> findOneWithToOneRelationships(@Param("id") Long id);
}
