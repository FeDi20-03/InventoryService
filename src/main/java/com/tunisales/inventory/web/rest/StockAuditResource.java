package com.tunisales.inventory.web.rest;

import com.tunisales.inventory.repository.StockAuditRepository;
import com.tunisales.inventory.service.StockAuditQueryService;
import com.tunisales.inventory.service.StockAuditService;
import com.tunisales.inventory.service.criteria.StockAuditCriteria;
import com.tunisales.inventory.service.dto.StockAuditDTO;
import com.tunisales.inventory.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.tunisales.inventory.domain.StockAudit}.
 */
@RestController
@RequestMapping("/api")
public class StockAuditResource {

    private final Logger log = LoggerFactory.getLogger(StockAuditResource.class);

    private static final String ENTITY_NAME = "inventoryServiceStockAudit";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StockAuditService stockAuditService;

    private final StockAuditRepository stockAuditRepository;

    private final StockAuditQueryService stockAuditQueryService;

    public StockAuditResource(
        StockAuditService stockAuditService,
        StockAuditRepository stockAuditRepository,
        StockAuditQueryService stockAuditQueryService
    ) {
        this.stockAuditService = stockAuditService;
        this.stockAuditRepository = stockAuditRepository;
        this.stockAuditQueryService = stockAuditQueryService;
    }

    /**
     * {@code POST  /stock-audits} : Create a new stockAudit.
     *
     * @param stockAuditDTO the stockAuditDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stockAuditDTO, or with status {@code 400 (Bad Request)} if the stockAudit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/stock-audits")
    public ResponseEntity<StockAuditDTO> createStockAudit(@Valid @RequestBody StockAuditDTO stockAuditDTO) throws URISyntaxException {
        log.debug("REST request to save StockAudit : {}", stockAuditDTO);
        if (stockAuditDTO.getId() != null) {
            throw new BadRequestAlertException("A new stockAudit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        StockAuditDTO result = stockAuditService.save(stockAuditDTO);
        return ResponseEntity
            .created(new URI("/api/stock-audits/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /stock-audits/:id} : Updates an existing stockAudit.
     *
     * @param id the id of the stockAuditDTO to save.
     * @param stockAuditDTO the stockAuditDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockAuditDTO,
     * or with status {@code 400 (Bad Request)} if the stockAuditDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stockAuditDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/stock-audits/{id}")
    public ResponseEntity<StockAuditDTO> updateStockAudit(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StockAuditDTO stockAuditDTO
    ) throws URISyntaxException {
        log.debug("REST request to update StockAudit : {}, {}", id, stockAuditDTO);
        if (stockAuditDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockAuditDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockAuditRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        StockAuditDTO result = stockAuditService.update(stockAuditDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockAuditDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /stock-audits/:id} : Partial updates given fields of an existing stockAudit, field will ignore if it is null
     *
     * @param id the id of the stockAuditDTO to save.
     * @param stockAuditDTO the stockAuditDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockAuditDTO,
     * or with status {@code 400 (Bad Request)} if the stockAuditDTO is not valid,
     * or with status {@code 404 (Not Found)} if the stockAuditDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the stockAuditDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/stock-audits/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StockAuditDTO> partialUpdateStockAudit(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StockAuditDTO stockAuditDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update StockAudit partially : {}, {}", id, stockAuditDTO);
        if (stockAuditDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockAuditDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockAuditRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StockAuditDTO> result = stockAuditService.partialUpdate(stockAuditDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockAuditDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /stock-audits} : get all the stockAudits.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stockAudits in body.
     */
    @GetMapping("/stock-audits")
    public ResponseEntity<List<StockAuditDTO>> getAllStockAudits(
        StockAuditCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get StockAudits by criteria: {}", criteria);
        Page<StockAuditDTO> page = stockAuditQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /stock-audits/count} : count all the stockAudits.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/stock-audits/count")
    public ResponseEntity<Long> countStockAudits(StockAuditCriteria criteria) {
        log.debug("REST request to count StockAudits by criteria: {}", criteria);
        return ResponseEntity.ok().body(stockAuditQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /stock-audits/:id} : get the "id" stockAudit.
     *
     * @param id the id of the stockAuditDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stockAuditDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/stock-audits/{id}")
    public ResponseEntity<StockAuditDTO> getStockAudit(@PathVariable Long id) {
        log.debug("REST request to get StockAudit : {}", id);
        Optional<StockAuditDTO> stockAuditDTO = stockAuditService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stockAuditDTO);
    }

    /**
     * {@code DELETE  /stock-audits/:id} : delete the "id" stockAudit.
     *
     * @param id the id of the stockAuditDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/stock-audits/{id}")
    public ResponseEntity<Void> deleteStockAudit(@PathVariable Long id) {
        log.debug("REST request to delete StockAudit : {}", id);
        stockAuditService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
