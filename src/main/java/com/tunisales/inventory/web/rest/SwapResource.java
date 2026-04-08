package com.tunisales.inventory.web.rest;

import com.tunisales.inventory.repository.SwapRepository;
import com.tunisales.inventory.service.SwapQueryService;
import com.tunisales.inventory.service.SwapService;
import com.tunisales.inventory.service.criteria.SwapCriteria;
import com.tunisales.inventory.service.dto.SwapDTO;
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
 * REST controller for managing {@link com.tunisales.inventory.domain.Swap}.
 */
@RestController
@RequestMapping("/api")
public class SwapResource {

    private final Logger log = LoggerFactory.getLogger(SwapResource.class);

    private static final String ENTITY_NAME = "inventoryServiceSwap";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SwapService swapService;

    private final SwapRepository swapRepository;

    private final SwapQueryService swapQueryService;

    public SwapResource(SwapService swapService, SwapRepository swapRepository, SwapQueryService swapQueryService) {
        this.swapService = swapService;
        this.swapRepository = swapRepository;
        this.swapQueryService = swapQueryService;
    }

    /**
     * {@code POST  /swaps} : Create a new swap.
     *
     * @param swapDTO the swapDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new swapDTO, or with status {@code 400 (Bad Request)} if the swap has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/swaps")
    public ResponseEntity<SwapDTO> createSwap(@Valid @RequestBody SwapDTO swapDTO) throws URISyntaxException {
        log.debug("REST request to save Swap : {}", swapDTO);
        if (swapDTO.getId() != null) {
            throw new BadRequestAlertException("A new swap cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SwapDTO result = swapService.save(swapDTO);
        return ResponseEntity
            .created(new URI("/api/swaps/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /swaps/:id} : Updates an existing swap.
     *
     * @param id the id of the swapDTO to save.
     * @param swapDTO the swapDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated swapDTO,
     * or with status {@code 400 (Bad Request)} if the swapDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the swapDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/swaps/{id}")
    public ResponseEntity<SwapDTO> updateSwap(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SwapDTO swapDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Swap : {}, {}", id, swapDTO);
        if (swapDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, swapDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!swapRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SwapDTO result = swapService.update(swapDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, swapDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /swaps/:id} : Partial updates given fields of an existing swap, field will ignore if it is null
     *
     * @param id the id of the swapDTO to save.
     * @param swapDTO the swapDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated swapDTO,
     * or with status {@code 400 (Bad Request)} if the swapDTO is not valid,
     * or with status {@code 404 (Not Found)} if the swapDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the swapDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/swaps/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SwapDTO> partialUpdateSwap(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SwapDTO swapDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Swap partially : {}, {}", id, swapDTO);
        if (swapDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, swapDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!swapRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SwapDTO> result = swapService.partialUpdate(swapDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, swapDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /swaps} : get all the swaps.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of swaps in body.
     */
    @GetMapping("/swaps")
    public ResponseEntity<List<SwapDTO>> getAllSwaps(
        SwapCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Swaps by criteria: {}", criteria);
        Page<SwapDTO> page = swapQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /swaps/count} : count all the swaps.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/swaps/count")
    public ResponseEntity<Long> countSwaps(SwapCriteria criteria) {
        log.debug("REST request to count Swaps by criteria: {}", criteria);
        return ResponseEntity.ok().body(swapQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /swaps/:id} : get the "id" swap.
     *
     * @param id the id of the swapDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the swapDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/swaps/{id}")
    public ResponseEntity<SwapDTO> getSwap(@PathVariable Long id) {
        log.debug("REST request to get Swap : {}", id);
        Optional<SwapDTO> swapDTO = swapService.findOne(id);
        return ResponseUtil.wrapOrNotFound(swapDTO);
    }

    /**
     * {@code DELETE  /swaps/:id} : delete the "id" swap.
     *
     * @param id the id of the swapDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/swaps/{id}")
    public ResponseEntity<Void> deleteSwap(@PathVariable Long id) {
        log.debug("REST request to delete Swap : {}", id);
        swapService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
