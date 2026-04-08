package com.tunisales.inventory.web.rest;

import com.tunisales.inventory.repository.StockItemRepository;
import com.tunisales.inventory.service.StockItemQueryService;
import com.tunisales.inventory.service.StockItemService;
import com.tunisales.inventory.service.criteria.StockItemCriteria;
import com.tunisales.inventory.service.dto.StockItemDTO;
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
 * REST controller for managing {@link com.tunisales.inventory.domain.StockItem}.
 */
@RestController
@RequestMapping("/api")
public class StockItemResource {

    private final Logger log = LoggerFactory.getLogger(StockItemResource.class);

    private static final String ENTITY_NAME = "inventoryServiceStockItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StockItemService stockItemService;

    private final StockItemRepository stockItemRepository;

    private final StockItemQueryService stockItemQueryService;

    public StockItemResource(
        StockItemService stockItemService,
        StockItemRepository stockItemRepository,
        StockItemQueryService stockItemQueryService
    ) {
        this.stockItemService = stockItemService;
        this.stockItemRepository = stockItemRepository;
        this.stockItemQueryService = stockItemQueryService;
    }

    /**
     * {@code POST  /stock-items} : Create a new stockItem.
     *
     * @param stockItemDTO the stockItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stockItemDTO, or with status {@code 400 (Bad Request)} if the stockItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/stock-items")
    public ResponseEntity<StockItemDTO> createStockItem(@Valid @RequestBody StockItemDTO stockItemDTO) throws URISyntaxException {
        log.debug("REST request to save StockItem : {}", stockItemDTO);
        if (stockItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new stockItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        StockItemDTO result = stockItemService.save(stockItemDTO);
        return ResponseEntity
            .created(new URI("/api/stock-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /stock-items/:id} : Updates an existing stockItem.
     *
     * @param id the id of the stockItemDTO to save.
     * @param stockItemDTO the stockItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockItemDTO,
     * or with status {@code 400 (Bad Request)} if the stockItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stockItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/stock-items/{id}")
    public ResponseEntity<StockItemDTO> updateStockItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StockItemDTO stockItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to update StockItem : {}, {}", id, stockItemDTO);
        if (stockItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        StockItemDTO result = stockItemService.update(stockItemDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockItemDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /stock-items/:id} : Partial updates given fields of an existing stockItem, field will ignore if it is null
     *
     * @param id the id of the stockItemDTO to save.
     * @param stockItemDTO the stockItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockItemDTO,
     * or with status {@code 400 (Bad Request)} if the stockItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the stockItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the stockItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/stock-items/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StockItemDTO> partialUpdateStockItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StockItemDTO stockItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update StockItem partially : {}, {}", id, stockItemDTO);
        if (stockItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StockItemDTO> result = stockItemService.partialUpdate(stockItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /stock-items} : get all the stockItems.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stockItems in body.
     */
    @GetMapping("/stock-items")
    public ResponseEntity<List<StockItemDTO>> getAllStockItems(
        StockItemCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get StockItems by criteria: {}", criteria);
        Page<StockItemDTO> page = stockItemQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /stock-items/count} : count all the stockItems.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/stock-items/count")
    public ResponseEntity<Long> countStockItems(StockItemCriteria criteria) {
        log.debug("REST request to count StockItems by criteria: {}", criteria);
        return ResponseEntity.ok().body(stockItemQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /stock-items/:id} : get the "id" stockItem.
     *
     * @param id the id of the stockItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stockItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/stock-items/{id}")
    public ResponseEntity<StockItemDTO> getStockItem(@PathVariable Long id) {
        log.debug("REST request to get StockItem : {}", id);
        Optional<StockItemDTO> stockItemDTO = stockItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stockItemDTO);
    }

    /**
     * {@code DELETE  /stock-items/:id} : delete the "id" stockItem.
     *
     * @param id the id of the stockItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/stock-items/{id}")
    public ResponseEntity<Void> deleteStockItem(@PathVariable Long id) {
        log.debug("REST request to delete StockItem : {}", id);
        stockItemService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
