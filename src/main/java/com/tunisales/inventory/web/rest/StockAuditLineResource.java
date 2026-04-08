package com.tunisales.inventory.web.rest;

import com.tunisales.inventory.repository.StockAuditLineRepository;
import com.tunisales.inventory.service.StockAuditLineService;
import com.tunisales.inventory.service.dto.StockAuditLineDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.tunisales.inventory.domain.StockAuditLine}.
 */
@RestController
@RequestMapping("/api")
public class StockAuditLineResource {

    private final Logger log = LoggerFactory.getLogger(StockAuditLineResource.class);

    private static final String ENTITY_NAME = "inventoryServiceStockAuditLine";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StockAuditLineService stockAuditLineService;

    private final StockAuditLineRepository stockAuditLineRepository;

    public StockAuditLineResource(StockAuditLineService stockAuditLineService, StockAuditLineRepository stockAuditLineRepository) {
        this.stockAuditLineService = stockAuditLineService;
        this.stockAuditLineRepository = stockAuditLineRepository;
    }

    /**
     * {@code POST  /stock-audit-lines} : Create a new stockAuditLine.
     *
     * @param stockAuditLineDTO the stockAuditLineDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stockAuditLineDTO, or with status {@code 400 (Bad Request)} if the stockAuditLine has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/stock-audit-lines")
    public ResponseEntity<StockAuditLineDTO> createStockAuditLine(@Valid @RequestBody StockAuditLineDTO stockAuditLineDTO)
        throws URISyntaxException {
        log.debug("REST request to save StockAuditLine : {}", stockAuditLineDTO);
        if (stockAuditLineDTO.getId() != null) {
            throw new BadRequestAlertException("A new stockAuditLine cannot already have an ID", ENTITY_NAME, "idexists");
        }
        StockAuditLineDTO result = stockAuditLineService.save(stockAuditLineDTO);
        return ResponseEntity
            .created(new URI("/api/stock-audit-lines/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /stock-audit-lines/:id} : Updates an existing stockAuditLine.
     *
     * @param id the id of the stockAuditLineDTO to save.
     * @param stockAuditLineDTO the stockAuditLineDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockAuditLineDTO,
     * or with status {@code 400 (Bad Request)} if the stockAuditLineDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stockAuditLineDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/stock-audit-lines/{id}")
    public ResponseEntity<StockAuditLineDTO> updateStockAuditLine(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StockAuditLineDTO stockAuditLineDTO
    ) throws URISyntaxException {
        log.debug("REST request to update StockAuditLine : {}, {}", id, stockAuditLineDTO);
        if (stockAuditLineDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockAuditLineDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockAuditLineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        StockAuditLineDTO result = stockAuditLineService.update(stockAuditLineDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockAuditLineDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /stock-audit-lines/:id} : Partial updates given fields of an existing stockAuditLine, field will ignore if it is null
     *
     * @param id the id of the stockAuditLineDTO to save.
     * @param stockAuditLineDTO the stockAuditLineDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockAuditLineDTO,
     * or with status {@code 400 (Bad Request)} if the stockAuditLineDTO is not valid,
     * or with status {@code 404 (Not Found)} if the stockAuditLineDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the stockAuditLineDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/stock-audit-lines/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StockAuditLineDTO> partialUpdateStockAuditLine(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StockAuditLineDTO stockAuditLineDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update StockAuditLine partially : {}, {}", id, stockAuditLineDTO);
        if (stockAuditLineDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockAuditLineDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockAuditLineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StockAuditLineDTO> result = stockAuditLineService.partialUpdate(stockAuditLineDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockAuditLineDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /stock-audit-lines} : get all the stockAuditLines.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stockAuditLines in body.
     */
    @GetMapping("/stock-audit-lines")
    public ResponseEntity<List<StockAuditLineDTO>> getAllStockAuditLines(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of StockAuditLines");
        Page<StockAuditLineDTO> page;
        if (eagerload) {
            page = stockAuditLineService.findAllWithEagerRelationships(pageable);
        } else {
            page = stockAuditLineService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /stock-audit-lines/:id} : get the "id" stockAuditLine.
     *
     * @param id the id of the stockAuditLineDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stockAuditLineDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/stock-audit-lines/{id}")
    public ResponseEntity<StockAuditLineDTO> getStockAuditLine(@PathVariable Long id) {
        log.debug("REST request to get StockAuditLine : {}", id);
        Optional<StockAuditLineDTO> stockAuditLineDTO = stockAuditLineService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stockAuditLineDTO);
    }

    /**
     * {@code DELETE  /stock-audit-lines/:id} : delete the "id" stockAuditLine.
     *
     * @param id the id of the stockAuditLineDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/stock-audit-lines/{id}")
    public ResponseEntity<Void> deleteStockAuditLine(@PathVariable Long id) {
        log.debug("REST request to delete StockAuditLine : {}", id);
        stockAuditLineService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
