package com.tunisales.inventory.repository;

import com.tunisales.inventory.domain.Swap;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Swap entity.
 */
@Repository
public interface SwapRepository extends JpaRepository<Swap, Long>, JpaSpecificationExecutor<Swap> {
    default Optional<Swap> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Swap> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Swap> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct swap from Swap swap left join fetch swap.outgoingItem left join fetch swap.incomingItem",
        countQuery = "select count(distinct swap) from Swap swap"
    )
    Page<Swap> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct swap from Swap swap left join fetch swap.outgoingItem left join fetch swap.incomingItem")
    List<Swap> findAllWithToOneRelationships();

    @Query("select swap from Swap swap left join fetch swap.outgoingItem left join fetch swap.incomingItem where swap.id =:id")
    Optional<Swap> findOneWithToOneRelationships(@Param("id") Long id);
}
