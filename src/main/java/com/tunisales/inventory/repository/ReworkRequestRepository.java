package com.tunisales.inventory.repository;

import com.tunisales.inventory.domain.ReworkRequest;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link ReworkRequest} entity.
 */
@Repository
public interface ReworkRequestRepository extends JpaRepository<ReworkRequest, Long> {
    Optional<ReworkRequest> findFirstByExternalId(String externalId);
}
