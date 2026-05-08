package com.tunisales.inventory.web.rest;

import com.tunisales.inventory.repository.StockItemRepository;
import com.tunisales.inventory.security.AuthoritiesConstants;
import com.tunisales.inventory.security.SecurityUtils;
import com.tunisales.inventory.service.ReworkRequestService;
import com.tunisales.inventory.service.StockItemQueryService;
import com.tunisales.inventory.service.StockItemService;
import com.tunisales.inventory.service.criteria.StockItemCriteria;
import com.tunisales.inventory.service.dto.ReworkRequestDTO;
import com.tunisales.inventory.service.dto.StockItemDTO;
import com.tunisales.inventory.service.dto.StockItemScanDTO;
import com.tunisales.inventory.service.util.ImeiValidator;
import com.tunisales.inventory.web.rest.errors.BadRequestAlertException;
import com.tunisales.inventory.web.rest.vm.DeclareLostVM;
import com.tunisales.inventory.web.rest.vm.ImeiScanVM;
import com.tunisales.inventory.web.rest.vm.SendToReworkVM;
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

    private final ReworkRequestService reworkRequestService;

    public StockItemResource(
        StockItemService stockItemService,
        StockItemRepository stockItemRepository,
        StockItemQueryService stockItemQueryService,
        ReworkRequestService reworkRequestService
    ) {
        this.stockItemService = stockItemService;
        this.stockItemRepository = stockItemRepository;
        this.stockItemQueryService = stockItemQueryService;
        this.reworkRequestService = reworkRequestService;
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
     * {@code POST  /stock-items/scan} : look up a stock item by IMEI (doucheur workflow).
     *
     * <p>The IMEI is validated for length (15 digits) and Luhn checksum.
     * Returns the enriched {@link StockItemScanDTO} (warehouse name/type, status,
     * last movement timestamp).</p>
     *
     * @param scan request body containing the IMEI to scan.
     * @return {@code 200 OK} with the scan DTO,
     *         {@code 400 Bad Request} if the IMEI is syntactically invalid,
     *         {@code 404 Not Found} if no stock item matches.
     */
    @PostMapping("/stock-items/scan")
    @PreAuthorize(
        "hasAnyAuthority(\"" +
        AuthoritiesConstants.ADMIN +
        "\", \"" +
        AuthoritiesConstants.ADMIN_COMMERCIAL +
        "\", \"" +
        AuthoritiesConstants.ADMIN_SYSTEME +
        "\", \"" +
        AuthoritiesConstants.COMMERCIAL +
        "\", \"" +
        AuthoritiesConstants.MAGASINIER +
        "\")"
    )
    public ResponseEntity<StockItemScanDTO> scanStockItem(@Valid @RequestBody ImeiScanVM scan) {
        log.debug("REST request to scan StockItem by IMEI : {}", scan.getImei());
        if (!ImeiValidator.isValid(scan.getImei())) {
            throw new BadRequestAlertException("Invalid IMEI", ENTITY_NAME, "imeiinvalid");
        }
        return stockItemService
            .scanByImei(scan.getImei())
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown IMEI"));
    }

    /**
     * {@code POST  /stock-items/{id}/declare-lost} : declare an item LOST.
     *
     * <p>The item must currently be in {@code MISSING} status; otherwise a
     * {@code 409 Conflict} is returned.</p>
     */
    @PostMapping("/stock-items/{id}/declare-lost")
    @PreAuthorize(
        "hasAnyAuthority(\"" +
        AuthoritiesConstants.ADMIN +
        "\", \"" +
        AuthoritiesConstants.ADMIN_COMMERCIAL +
        "\", \"" +
        AuthoritiesConstants.ADMIN_SYSTEME +
        "\")"
    )
    public ResponseEntity<StockItemDTO> declareLost(@PathVariable Long id, @Valid @RequestBody(required = false) DeclareLostVM body) {
        log.debug("REST request to declare StockItem {} as LOST", id);
        String reason = body != null ? body.getReason() : null;
        String declaredBy = SecurityUtils.getCurrentUserLogin().orElse("system");
        try {
            StockItemDTO result = stockItemService.declareLost(id, declaredBy, reason);
            return ResponseEntity.ok(result);
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
    }

    /**
     * {@code POST  /stock-items/{id}/mark-repaired} : mark a DEFECTIVE item as repaired.
     */
    @PostMapping("/stock-items/{id}/mark-repaired")
    @PreAuthorize(
        "hasAnyAuthority(\"" +
        AuthoritiesConstants.ADMIN +
        "\", \"" +
        AuthoritiesConstants.ADMIN_COMMERCIAL +
        "\", \"" +
        AuthoritiesConstants.ADMIN_SYSTEME +
        "\", \"" +
        AuthoritiesConstants.MAGASINIER +
        "\")"
    )
    public ResponseEntity<StockItemDTO> markRepaired(@PathVariable Long id) {
        log.debug("REST request to mark StockItem {} as repaired", id);
        try {
            StockItemDTO result = stockItemService.markRepaired(id);
            return ResponseEntity.ok(result);
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
    }

    /**
     * {@code POST  /stock-items/{id}/send-to-rework} : send a defective item to the GPF rework gateway.
     */
    @PostMapping("/stock-items/{id}/send-to-rework")
    @PreAuthorize(
        "hasAnyAuthority(\"" +
        AuthoritiesConstants.ADMIN +
        "\", \"" +
        AuthoritiesConstants.ADMIN_COMMERCIAL +
        "\", \"" +
        AuthoritiesConstants.ADMIN_SYSTEME +
        "\", \"" +
        AuthoritiesConstants.MAGASINIER +
        "\")"
    )
    public ResponseEntity<ReworkRequestDTO> sendToRework(@PathVariable Long id, @Valid @RequestBody(required = false) SendToReworkVM body) {
        log.debug("REST request to send StockItem {} to rework", id);
        String reason = body != null ? body.getReason() : null;
        try {
            ReworkRequestDTO dto = reworkRequestService.sendToRework(id, reason);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(dto);
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
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
