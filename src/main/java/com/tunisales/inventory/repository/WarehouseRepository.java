package com.tunisales.inventory.repository;

import com.tunisales.inventory.domain.Warehouse;
import com.tunisales.inventory.domain.enumeration.WarehouseType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Warehouse entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long>, JpaSpecificationExecutor<Warehouse> {
    Optional<Warehouse> findFirstByNameAndIsActiveTrue(String name);

    Optional<Warehouse> findFirstByTypeAndIsActiveTrueOrderByIdAsc(WarehouseType type);

    List<Warehouse> findAllByTypeAndIsActiveTrue(WarehouseType type);
}
