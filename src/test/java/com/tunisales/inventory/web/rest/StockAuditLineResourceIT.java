package com.tunisales.inventory.web.rest;

import static com.tunisales.inventory.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tunisales.inventory.IntegrationTest;
import com.tunisales.inventory.domain.StockAudit;
import com.tunisales.inventory.domain.StockAuditLine;
import com.tunisales.inventory.domain.StockItem;
import com.tunisales.inventory.domain.enumeration.AuditResolution;
import com.tunisales.inventory.repository.StockAuditLineRepository;
import com.tunisales.inventory.service.StockAuditLineService;
import com.tunisales.inventory.service.dto.StockAuditLineDTO;
import com.tunisales.inventory.service.mapper.StockAuditLineMapper;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link StockAuditLineResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StockAuditLineResourceIT {

    private static final Boolean DEFAULT_FOUND_PHYSICALLY = false;
    private static final Boolean UPDATED_FOUND_PHYSICALLY = true;

    private static final AuditResolution DEFAULT_RESOLUTION = AuditResolution.FOUND;
    private static final AuditResolution UPDATED_RESOLUTION = AuditResolution.LOST_STOLEN;

    private static final String DEFAULT_RESOLUTION_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_RESOLUTION_NOTE = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/stock-audit-lines";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StockAuditLineRepository stockAuditLineRepository;

    @Mock
    private StockAuditLineRepository stockAuditLineRepositoryMock;

    @Autowired
    private StockAuditLineMapper stockAuditLineMapper;

    @Mock
    private StockAuditLineService stockAuditLineServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStockAuditLineMockMvc;

    private StockAuditLine stockAuditLine;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockAuditLine createEntity(EntityManager em) {
        StockAuditLine stockAuditLine = new StockAuditLine()
            .foundPhysically(DEFAULT_FOUND_PHYSICALLY)
            .resolution(DEFAULT_RESOLUTION)
            .resolutionNote(DEFAULT_RESOLUTION_NOTE)
            .createdAt(DEFAULT_CREATED_AT);
        // Add required entity
        StockItem stockItem;
        if (TestUtil.findAll(em, StockItem.class).isEmpty()) {
            stockItem = StockItemResourceIT.createEntity(em);
            em.persist(stockItem);
            em.flush();
        } else {
            stockItem = TestUtil.findAll(em, StockItem.class).get(0);
        }
        stockAuditLine.setStockItem(stockItem);
        // Add required entity
        StockAudit stockAudit;
        if (TestUtil.findAll(em, StockAudit.class).isEmpty()) {
            stockAudit = StockAuditResourceIT.createEntity(em);
            em.persist(stockAudit);
            em.flush();
        } else {
            stockAudit = TestUtil.findAll(em, StockAudit.class).get(0);
        }
        stockAuditLine.setAudit(stockAudit);
        return stockAuditLine;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockAuditLine createUpdatedEntity(EntityManager em) {
        StockAuditLine stockAuditLine = new StockAuditLine()
            .foundPhysically(UPDATED_FOUND_PHYSICALLY)
            .resolution(UPDATED_RESOLUTION)
            .resolutionNote(UPDATED_RESOLUTION_NOTE)
            .createdAt(UPDATED_CREATED_AT);
        // Add required entity
        StockItem stockItem;
        if (TestUtil.findAll(em, StockItem.class).isEmpty()) {
            stockItem = StockItemResourceIT.createUpdatedEntity(em);
            em.persist(stockItem);
            em.flush();
        } else {
            stockItem = TestUtil.findAll(em, StockItem.class).get(0);
        }
        stockAuditLine.setStockItem(stockItem);
        // Add required entity
        StockAudit stockAudit;
        if (TestUtil.findAll(em, StockAudit.class).isEmpty()) {
            stockAudit = StockAuditResourceIT.createUpdatedEntity(em);
            em.persist(stockAudit);
            em.flush();
        } else {
            stockAudit = TestUtil.findAll(em, StockAudit.class).get(0);
        }
        stockAuditLine.setAudit(stockAudit);
        return stockAuditLine;
    }

    @BeforeEach
    public void initTest() {
        stockAuditLine = createEntity(em);
    }

    @Test
    @Transactional
    void createStockAuditLine() throws Exception {
        int databaseSizeBeforeCreate = stockAuditLineRepository.findAll().size();
        // Create the StockAuditLine
        StockAuditLineDTO stockAuditLineDTO = stockAuditLineMapper.toDto(stockAuditLine);
        restStockAuditLineMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockAuditLineDTO))
            )
            .andExpect(status().isCreated());

        // Validate the StockAuditLine in the database
        List<StockAuditLine> stockAuditLineList = stockAuditLineRepository.findAll();
        assertThat(stockAuditLineList).hasSize(databaseSizeBeforeCreate + 1);
        StockAuditLine testStockAuditLine = stockAuditLineList.get(stockAuditLineList.size() - 1);
        assertThat(testStockAuditLine.getFoundPhysically()).isEqualTo(DEFAULT_FOUND_PHYSICALLY);
        assertThat(testStockAuditLine.getResolution()).isEqualTo(DEFAULT_RESOLUTION);
        assertThat(testStockAuditLine.getResolutionNote()).isEqualTo(DEFAULT_RESOLUTION_NOTE);
        assertThat(testStockAuditLine.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void createStockAuditLineWithExistingId() throws Exception {
        // Create the StockAuditLine with an existing ID
        stockAuditLine.setId(1L);
        StockAuditLineDTO stockAuditLineDTO = stockAuditLineMapper.toDto(stockAuditLine);

        int databaseSizeBeforeCreate = stockAuditLineRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStockAuditLineMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockAuditLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockAuditLine in the database
        List<StockAuditLine> stockAuditLineList = stockAuditLineRepository.findAll();
        assertThat(stockAuditLineList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFoundPhysicallyIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockAuditLineRepository.findAll().size();
        // set the field null
        stockAuditLine.setFoundPhysically(null);

        // Create the StockAuditLine, which fails.
        StockAuditLineDTO stockAuditLineDTO = stockAuditLineMapper.toDto(stockAuditLine);

        restStockAuditLineMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockAuditLineDTO))
            )
            .andExpect(status().isBadRequest());

        List<StockAuditLine> stockAuditLineList = stockAuditLineRepository.findAll();
        assertThat(stockAuditLineList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockAuditLineRepository.findAll().size();
        // set the field null
        stockAuditLine.setCreatedAt(null);

        // Create the StockAuditLine, which fails.
        StockAuditLineDTO stockAuditLineDTO = stockAuditLineMapper.toDto(stockAuditLine);

        restStockAuditLineMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockAuditLineDTO))
            )
            .andExpect(status().isBadRequest());

        List<StockAuditLine> stockAuditLineList = stockAuditLineRepository.findAll();
        assertThat(stockAuditLineList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStockAuditLines() throws Exception {
        // Initialize the database
        stockAuditLineRepository.saveAndFlush(stockAuditLine);

        // Get all the stockAuditLineList
        restStockAuditLineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockAuditLine.getId().intValue())))
            .andExpect(jsonPath("$.[*].foundPhysically").value(hasItem(DEFAULT_FOUND_PHYSICALLY.booleanValue())))
            .andExpect(jsonPath("$.[*].resolution").value(hasItem(DEFAULT_RESOLUTION.toString())))
            .andExpect(jsonPath("$.[*].resolutionNote").value(hasItem(DEFAULT_RESOLUTION_NOTE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStockAuditLinesWithEagerRelationshipsIsEnabled() throws Exception {
        when(stockAuditLineServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStockAuditLineMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(stockAuditLineServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStockAuditLinesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(stockAuditLineServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStockAuditLineMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(stockAuditLineRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getStockAuditLine() throws Exception {
        // Initialize the database
        stockAuditLineRepository.saveAndFlush(stockAuditLine);

        // Get the stockAuditLine
        restStockAuditLineMockMvc
            .perform(get(ENTITY_API_URL_ID, stockAuditLine.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stockAuditLine.getId().intValue()))
            .andExpect(jsonPath("$.foundPhysically").value(DEFAULT_FOUND_PHYSICALLY.booleanValue()))
            .andExpect(jsonPath("$.resolution").value(DEFAULT_RESOLUTION.toString()))
            .andExpect(jsonPath("$.resolutionNote").value(DEFAULT_RESOLUTION_NOTE))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)));
    }

    @Test
    @Transactional
    void getNonExistingStockAuditLine() throws Exception {
        // Get the stockAuditLine
        restStockAuditLineMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStockAuditLine() throws Exception {
        // Initialize the database
        stockAuditLineRepository.saveAndFlush(stockAuditLine);

        int databaseSizeBeforeUpdate = stockAuditLineRepository.findAll().size();

        // Update the stockAuditLine
        StockAuditLine updatedStockAuditLine = stockAuditLineRepository.findById(stockAuditLine.getId()).get();
        // Disconnect from session so that the updates on updatedStockAuditLine are not directly saved in db
        em.detach(updatedStockAuditLine);
        updatedStockAuditLine
            .foundPhysically(UPDATED_FOUND_PHYSICALLY)
            .resolution(UPDATED_RESOLUTION)
            .resolutionNote(UPDATED_RESOLUTION_NOTE)
            .createdAt(UPDATED_CREATED_AT);
        StockAuditLineDTO stockAuditLineDTO = stockAuditLineMapper.toDto(updatedStockAuditLine);

        restStockAuditLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockAuditLineDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockAuditLineDTO))
            )
            .andExpect(status().isOk());

        // Validate the StockAuditLine in the database
        List<StockAuditLine> stockAuditLineList = stockAuditLineRepository.findAll();
        assertThat(stockAuditLineList).hasSize(databaseSizeBeforeUpdate);
        StockAuditLine testStockAuditLine = stockAuditLineList.get(stockAuditLineList.size() - 1);
        assertThat(testStockAuditLine.getFoundPhysically()).isEqualTo(UPDATED_FOUND_PHYSICALLY);
        assertThat(testStockAuditLine.getResolution()).isEqualTo(UPDATED_RESOLUTION);
        assertThat(testStockAuditLine.getResolutionNote()).isEqualTo(UPDATED_RESOLUTION_NOTE);
        assertThat(testStockAuditLine.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingStockAuditLine() throws Exception {
        int databaseSizeBeforeUpdate = stockAuditLineRepository.findAll().size();
        stockAuditLine.setId(count.incrementAndGet());

        // Create the StockAuditLine
        StockAuditLineDTO stockAuditLineDTO = stockAuditLineMapper.toDto(stockAuditLine);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockAuditLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockAuditLineDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockAuditLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockAuditLine in the database
        List<StockAuditLine> stockAuditLineList = stockAuditLineRepository.findAll();
        assertThat(stockAuditLineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStockAuditLine() throws Exception {
        int databaseSizeBeforeUpdate = stockAuditLineRepository.findAll().size();
        stockAuditLine.setId(count.incrementAndGet());

        // Create the StockAuditLine
        StockAuditLineDTO stockAuditLineDTO = stockAuditLineMapper.toDto(stockAuditLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockAuditLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockAuditLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockAuditLine in the database
        List<StockAuditLine> stockAuditLineList = stockAuditLineRepository.findAll();
        assertThat(stockAuditLineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStockAuditLine() throws Exception {
        int databaseSizeBeforeUpdate = stockAuditLineRepository.findAll().size();
        stockAuditLine.setId(count.incrementAndGet());

        // Create the StockAuditLine
        StockAuditLineDTO stockAuditLineDTO = stockAuditLineMapper.toDto(stockAuditLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockAuditLineMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockAuditLineDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockAuditLine in the database
        List<StockAuditLine> stockAuditLineList = stockAuditLineRepository.findAll();
        assertThat(stockAuditLineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStockAuditLineWithPatch() throws Exception {
        // Initialize the database
        stockAuditLineRepository.saveAndFlush(stockAuditLine);

        int databaseSizeBeforeUpdate = stockAuditLineRepository.findAll().size();

        // Update the stockAuditLine using partial update
        StockAuditLine partialUpdatedStockAuditLine = new StockAuditLine();
        partialUpdatedStockAuditLine.setId(stockAuditLine.getId());

        partialUpdatedStockAuditLine.resolution(UPDATED_RESOLUTION);

        restStockAuditLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockAuditLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStockAuditLine))
            )
            .andExpect(status().isOk());

        // Validate the StockAuditLine in the database
        List<StockAuditLine> stockAuditLineList = stockAuditLineRepository.findAll();
        assertThat(stockAuditLineList).hasSize(databaseSizeBeforeUpdate);
        StockAuditLine testStockAuditLine = stockAuditLineList.get(stockAuditLineList.size() - 1);
        assertThat(testStockAuditLine.getFoundPhysically()).isEqualTo(DEFAULT_FOUND_PHYSICALLY);
        assertThat(testStockAuditLine.getResolution()).isEqualTo(UPDATED_RESOLUTION);
        assertThat(testStockAuditLine.getResolutionNote()).isEqualTo(DEFAULT_RESOLUTION_NOTE);
        assertThat(testStockAuditLine.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void fullUpdateStockAuditLineWithPatch() throws Exception {
        // Initialize the database
        stockAuditLineRepository.saveAndFlush(stockAuditLine);

        int databaseSizeBeforeUpdate = stockAuditLineRepository.findAll().size();

        // Update the stockAuditLine using partial update
        StockAuditLine partialUpdatedStockAuditLine = new StockAuditLine();
        partialUpdatedStockAuditLine.setId(stockAuditLine.getId());

        partialUpdatedStockAuditLine
            .foundPhysically(UPDATED_FOUND_PHYSICALLY)
            .resolution(UPDATED_RESOLUTION)
            .resolutionNote(UPDATED_RESOLUTION_NOTE)
            .createdAt(UPDATED_CREATED_AT);

        restStockAuditLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockAuditLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStockAuditLine))
            )
            .andExpect(status().isOk());

        // Validate the StockAuditLine in the database
        List<StockAuditLine> stockAuditLineList = stockAuditLineRepository.findAll();
        assertThat(stockAuditLineList).hasSize(databaseSizeBeforeUpdate);
        StockAuditLine testStockAuditLine = stockAuditLineList.get(stockAuditLineList.size() - 1);
        assertThat(testStockAuditLine.getFoundPhysically()).isEqualTo(UPDATED_FOUND_PHYSICALLY);
        assertThat(testStockAuditLine.getResolution()).isEqualTo(UPDATED_RESOLUTION);
        assertThat(testStockAuditLine.getResolutionNote()).isEqualTo(UPDATED_RESOLUTION_NOTE);
        assertThat(testStockAuditLine.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingStockAuditLine() throws Exception {
        int databaseSizeBeforeUpdate = stockAuditLineRepository.findAll().size();
        stockAuditLine.setId(count.incrementAndGet());

        // Create the StockAuditLine
        StockAuditLineDTO stockAuditLineDTO = stockAuditLineMapper.toDto(stockAuditLine);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockAuditLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stockAuditLineDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockAuditLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockAuditLine in the database
        List<StockAuditLine> stockAuditLineList = stockAuditLineRepository.findAll();
        assertThat(stockAuditLineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStockAuditLine() throws Exception {
        int databaseSizeBeforeUpdate = stockAuditLineRepository.findAll().size();
        stockAuditLine.setId(count.incrementAndGet());

        // Create the StockAuditLine
        StockAuditLineDTO stockAuditLineDTO = stockAuditLineMapper.toDto(stockAuditLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockAuditLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockAuditLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockAuditLine in the database
        List<StockAuditLine> stockAuditLineList = stockAuditLineRepository.findAll();
        assertThat(stockAuditLineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStockAuditLine() throws Exception {
        int databaseSizeBeforeUpdate = stockAuditLineRepository.findAll().size();
        stockAuditLine.setId(count.incrementAndGet());

        // Create the StockAuditLine
        StockAuditLineDTO stockAuditLineDTO = stockAuditLineMapper.toDto(stockAuditLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockAuditLineMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockAuditLineDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockAuditLine in the database
        List<StockAuditLine> stockAuditLineList = stockAuditLineRepository.findAll();
        assertThat(stockAuditLineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStockAuditLine() throws Exception {
        // Initialize the database
        stockAuditLineRepository.saveAndFlush(stockAuditLine);

        int databaseSizeBeforeDelete = stockAuditLineRepository.findAll().size();

        // Delete the stockAuditLine
        restStockAuditLineMockMvc
            .perform(delete(ENTITY_API_URL_ID, stockAuditLine.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<StockAuditLine> stockAuditLineList = stockAuditLineRepository.findAll();
        assertThat(stockAuditLineList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
