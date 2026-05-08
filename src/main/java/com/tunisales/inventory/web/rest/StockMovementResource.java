package com.tunisales.inventory.web.rest;

import com.tunisales.inventory.repository.StockMovementRepository;
import com.tunisales.inventory.security.AuthoritiesConstants;
import com.tunisales.inventory.security.SecurityUtils;
import com.tunisales.inventory.service.StockMovementQueryService;
import com.tunisales.inventory.service.StockMovementService;
import com.tunisales.inventory.service.criteria.StockMovementCriteria;
import com.tunisales.inventory.service.dto.StockMovementDTO;
import com.tunisales.inventory.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.tunisales.inventory.domain.StockMovement}.
 */
@RestController
@RequestMapping("/api")
public class StockMovementResource {

    private final Logger log = LoggerFactory.getLogger(StockMovementResource.class);

    private static final String ENTITY_NAME = "inventoryServiceStockMovement";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StockMovementService stockMovementService;

    private final StockMovementRepository stockMovementRepository;

    private final StockMovementQueryService stockMovementQueryService;

    public StockMovementResource(
        StockMovementService stockMovementService,
        StockMovementRepository stockMovementRepository,
        StockMovementQueryService stockMovementQueryService
    ) {
        this.stockMovementService = stockMovementService;
        this.stockMovementRepository = stockMovementRepository;
        this.stockMovementQueryService = stockMovementQueryService;
    }

    /**
     * {@code POST  /stock-movements} : Create a new stockMovement.
     *
     * @param stockMovementDTO the stockMovementDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stockMovementDTO, or with status {@code 400 (Bad Request)} if the stockMovement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/stock-movements")
    public ResponseEntity<StockMovementDTO> createStockMovement(@Valid @RequestBody StockMovementDTO stockMovementDTO)
        throws URISyntaxException {
        log.debug("REST request to save StockMovement : {}", stockMovementDTO);
        if (stockMovementDTO.getId() != null) {
            throw new BadRequestAlertException("A new stockMovement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        StockMovementDTO result;
        try {
            result = stockMovementService.save(stockMovementDTO);
        } catch (IllegalStateException ex) {
            // 1.4 — outbound from a LOCAL warehouse blocked while the latest
            // INBOUND has not been validated by a commercial.
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
        return ResponseEntity
            .created(new URI("/api/stock-movements/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code POST /stock-movements/{id}/validate-by-commercial} : confirm an
     * INBOUND movement onto a LOCAL warehouse. Restricted to ROLE_COMMERCIAL.
     */
    @PostMapping("/stock-movements/{id}/validate-by-commercial")
    @PreAuthorize("hasAnyAuthority(\"" + AuthoritiesConstants.ADMIN + "\", \"" + AuthoritiesConstants.COMMERCIAL + "\")")
    public ResponseEntity<StockMovementDTO> validateByCommercial(@PathVariable Long id) {
        log.debug("REST request to validate movement {} by commercial", id);
        String login = SecurityUtils.getCurrentUserLogin().orElse(null);
        try {
            return ResponseEntity.ok(stockMovementService.validateByCommercial(id, login));
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
    }

    /**
     * {@code PUT  /stock-movements/:id} : Updates an existing stockMovement.
     *
     * @param id the id of the stockMovementDTO to save.
     * @param stockMovementDTO the stockMovementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockMovementDTO,
     * or with status {@code 400 (Bad Request)} if the stockMovementDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stockMovementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/stock-movements/{id}")
    public ResponseEntity<StockMovementDTO> updateStockMovement(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StockMovementDTO stockMovementDTO
    ) throws URISyntaxException {
        log.debug("REST request to update StockMovement : {}, {}", id, stockMovementDTO);
        if (stockMovementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockMovementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockMovementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        StockMovementDTO result = stockMovementService.update(stockMovementDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockMovementDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /stock-movements/:id} : Partial updates given fields of an existing stockMovement, field will ignore if it is null
     *
     * @param id the id of the stockMovementDTO to save.
     * @param stockMovementDTO the stockMovementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockMovementDTO,
     * or with status {@code 400 (Bad Request)} if the stockMovementDTO is not valid,
     * or with status {@code 404 (Not Found)} if the stockMovementDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the stockMovementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/stock-movements/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StockMovementDTO> partialUpdateStockMovement(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StockMovementDTO stockMovementDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update StockMovement partially : {}, {}", id, stockMovementDTO);
        if (stockMovementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockMovementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockMovementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StockMovementDTO> result = stockMovementService.partialUpdate(stockMovementDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockMovementDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /stock-movements} : get all the stockMovements.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stockMovements in body.
     */
    @GetMapping("/stock-movements")
    public ResponseEntity<List<StockMovementDTO>> getAllStockMovements(
        StockMovementCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get StockMovements by criteria: {}", criteria);
        Page<StockMovementDTO> page = stockMovementQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /stock-movements/count} : count all the stockMovements.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/stock-movements/count")
    public ResponseEntity<Long> countStockMovements(StockMovementCriteria criteria) {
        log.debug("REST request to count StockMovements by criteria: {}", criteria);
        return ResponseEntity.ok().body(stockMovementQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /stock-movements/:id} : get the "id" stockMovement.
     *
     * @param id the id of the stockMovementDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stockMovementDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/stock-movements/{id}")
    public ResponseEntity<StockMovementDTO> getStockMovement(@PathVariable Long id) {
        log.debug("REST request to get StockMovement : {}", id);
        Optional<StockMovementDTO> stockMovementDTO = stockMovementService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stockMovementDTO);
    }

    /**
     * {@code DELETE  /stock-movements/:id} : delete the "id" stockMovement.
     *
     * @param id the id of the stockMovementDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/stock-movements/{id}")
    public ResponseEntity<Void> deleteStockMovement(@PathVariable Long id) {
        log.debug("REST request to delete StockMovement : {}", id);
        stockMovementService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
