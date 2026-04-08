package com.tunisales.inventory.service;

import com.tunisales.inventory.domain.Swap;
import com.tunisales.inventory.repository.SwapRepository;
import com.tunisales.inventory.service.dto.SwapDTO;
import com.tunisales.inventory.service.mapper.SwapMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Swap}.
 */
@Service
@Transactional
public class SwapService {

    private final Logger log = LoggerFactory.getLogger(SwapService.class);

    private final SwapRepository swapRepository;

    private final SwapMapper swapMapper;

    public SwapService(SwapRepository swapRepository, SwapMapper swapMapper) {
        this.swapRepository = swapRepository;
        this.swapMapper = swapMapper;
    }

    /**
     * Save a swap.
     *
     * @param swapDTO the entity to save.
     * @return the persisted entity.
     */
    public SwapDTO save(SwapDTO swapDTO) {
        log.debug("Request to save Swap : {}", swapDTO);
        Swap swap = swapMapper.toEntity(swapDTO);
        swap = swapRepository.save(swap);
        return swapMapper.toDto(swap);
    }

    /**
     * Update a swap.
     *
     * @param swapDTO the entity to save.
     * @return the persisted entity.
     */
    public SwapDTO update(SwapDTO swapDTO) {
        log.debug("Request to update Swap : {}", swapDTO);
        Swap swap = swapMapper.toEntity(swapDTO);
        swap = swapRepository.save(swap);
        return swapMapper.toDto(swap);
    }

    /**
     * Partially update a swap.
     *
     * @param swapDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SwapDTO> partialUpdate(SwapDTO swapDTO) {
        log.debug("Request to partially update Swap : {}", swapDTO);

        return swapRepository
            .findById(swapDTO.getId())
            .map(existingSwap -> {
                swapMapper.partialUpdate(existingSwap, swapDTO);

                return existingSwap;
            })
            .map(swapRepository::save)
            .map(swapMapper::toDto);
    }

    /**
     * Get all the swaps.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SwapDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Swaps");
        return swapRepository.findAll(pageable).map(swapMapper::toDto);
    }

    /**
     * Get all the swaps with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<SwapDTO> findAllWithEagerRelationships(Pageable pageable) {
        return swapRepository.findAllWithEagerRelationships(pageable).map(swapMapper::toDto);
    }

    /**
     * Get one swap by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SwapDTO> findOne(Long id) {
        log.debug("Request to get Swap : {}", id);
        return swapRepository.findOneWithEagerRelationships(id).map(swapMapper::toDto);
    }

    /**
     * Delete the swap by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Swap : {}", id);
        swapRepository.deleteById(id);
    }
}
