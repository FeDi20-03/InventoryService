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
import com.tunisales.inventory.domain.Warehouse;
import com.tunisales.inventory.domain.enumeration.AuditStatus;
import com.tunisales.inventory.repository.StockAuditRepository;
import com.tunisales.inventory.service.StockAuditService;
import com.tunisales.inventory.service.criteria.StockAuditCriteria;
import com.tunisales.inventory.service.dto.StockAuditDTO;
import com.tunisales.inventory.service.mapper.StockAuditMapper;
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
 * Integration tests for the {@link StockAuditResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StockAuditResourceIT {

    private static final Long DEFAULT_TENANT_ID = 1L;
    private static final Long UPDATED_TENANT_ID = 2L;
    private static final Long SMALLER_TENANT_ID = 1L - 1L;

    private static final AuditStatus DEFAULT_STATUS = AuditStatus.IN_PROGRESS;
    private static final AuditStatus UPDATED_STATUS = AuditStatus.CLOSED;

    private static final Integer DEFAULT_THEORETICAL_COUNT = 0;
    private static final Integer UPDATED_THEORETICAL_COUNT = 1;
    private static final Integer SMALLER_THEORETICAL_COUNT = 0 - 1;

    private static final Integer DEFAULT_PHYSICAL_COUNT = 0;
    private static final Integer UPDATED_PHYSICAL_COUNT = 1;
    private static final Integer SMALLER_PHYSICAL_COUNT = 0 - 1;

    private static final Integer DEFAULT_DISCREPANCY_COUNT = 0;
    private static final Integer UPDATED_DISCREPANCY_COUNT = 1;
    private static final Integer SMALLER_DISCREPANCY_COUNT = 0 - 1;

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String DEFAULT_AUDITOR_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_AUDITOR_LOGIN = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_STARTED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_STARTED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_STARTED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_CLOSED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CLOSED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CLOSED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/stock-audits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StockAuditRepository stockAuditRepository;

    @Mock
    private StockAuditRepository stockAuditRepositoryMock;

    @Autowired
    private StockAuditMapper stockAuditMapper;

    @Mock
    private StockAuditService stockAuditServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStockAuditMockMvc;

    private StockAudit stockAudit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockAudit createEntity(EntityManager em) {
        StockAudit stockAudit = new StockAudit()
            .tenantId(DEFAULT_TENANT_ID)
            .status(DEFAULT_STATUS)
            .theoreticalCount(DEFAULT_THEORETICAL_COUNT)
            .physicalCount(DEFAULT_PHYSICAL_COUNT)
            .discrepancyCount(DEFAULT_DISCREPANCY_COUNT)
            .notes(DEFAULT_NOTES)
            .auditorLogin(DEFAULT_AUDITOR_LOGIN)
            .startedAt(DEFAULT_STARTED_AT)
            .closedAt(DEFAULT_CLOSED_AT);
        // Add required entity
        Warehouse warehouse;
        if (TestUtil.findAll(em, Warehouse.class).isEmpty()) {
            warehouse = WarehouseResourceIT.createEntity(em);
            em.persist(warehouse);
            em.flush();
        } else {
            warehouse = TestUtil.findAll(em, Warehouse.class).get(0);
        }
        stockAudit.setWarehouse(warehouse);
        return stockAudit;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockAudit createUpdatedEntity(EntityManager em) {
        StockAudit stockAudit = new StockAudit()
            .tenantId(UPDATED_TENANT_ID)
            .status(UPDATED_STATUS)
            .theoreticalCount(UPDATED_THEORETICAL_COUNT)
            .physicalCount(UPDATED_PHYSICAL_COUNT)
            .discrepancyCount(UPDATED_DISCREPANCY_COUNT)
            .notes(UPDATED_NOTES)
            .auditorLogin(UPDATED_AUDITOR_LOGIN)
            .startedAt(UPDATED_STARTED_AT)
            .closedAt(UPDATED_CLOSED_AT);
        // Add required entity
        Warehouse warehouse;
        if (TestUtil.findAll(em, Warehouse.class).isEmpty()) {
            warehouse = WarehouseResourceIT.createUpdatedEntity(em);
            em.persist(warehouse);
            em.flush();
        } else {
            warehouse = TestUtil.findAll(em, Warehouse.class).get(0);
        }
        stockAudit.setWarehouse(warehouse);
        return stockAudit;
    }

    @BeforeEach
    public void initTest() {
        stockAudit = createEntity(em);
    }

    @Test
    @Transactional
    void createStockAudit() throws Exception {
        int databaseSizeBeforeCreate = stockAuditRepository.findAll().size();
        // Create the StockAudit
        StockAuditDTO stockAuditDTO = stockAuditMapper.toDto(stockAudit);
        restStockAuditMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockAuditDTO)))
            .andExpect(status().isCreated());

        // Validate the StockAudit in the database
        List<StockAudit> stockAuditList = stockAuditRepository.findAll();
        assertThat(stockAuditList).hasSize(databaseSizeBeforeCreate + 1);
        StockAudit testStockAudit = stockAuditList.get(stockAuditList.size() - 1);
        assertThat(testStockAudit.getTenantId()).isEqualTo(DEFAULT_TENANT_ID);
        assertThat(testStockAudit.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testStockAudit.getTheoreticalCount()).isEqualTo(DEFAULT_THEORETICAL_COUNT);
        assertThat(testStockAudit.getPhysicalCount()).isEqualTo(DEFAULT_PHYSICAL_COUNT);
        assertThat(testStockAudit.getDiscrepancyCount()).isEqualTo(DEFAULT_DISCREPANCY_COUNT);
        assertThat(testStockAudit.getNotes()).isEqualTo(DEFAULT_NOTES);
        assertThat(testStockAudit.getAuditorLogin()).isEqualTo(DEFAULT_AUDITOR_LOGIN);
        assertThat(testStockAudit.getStartedAt()).isEqualTo(DEFAULT_STARTED_AT);
        assertThat(testStockAudit.getClosedAt()).isEqualTo(DEFAULT_CLOSED_AT);
    }

    @Test
    @Transactional
    void createStockAuditWithExistingId() throws Exception {
        // Create the StockAudit with an existing ID
        stockAudit.setId(1L);
        StockAuditDTO stockAuditDTO = stockAuditMapper.toDto(stockAudit);

        int databaseSizeBeforeCreate = stockAuditRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStockAuditMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockAuditDTO)))
            .andExpect(status().isBadRequest());

        // Validate the StockAudit in the database
        List<StockAudit> stockAuditList = stockAuditRepository.findAll();
        assertThat(stockAuditList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTenantIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockAuditRepository.findAll().size();
        // set the field null
        stockAudit.setTenantId(null);

        // Create the StockAudit, which fails.
        StockAuditDTO stockAuditDTO = stockAuditMapper.toDto(stockAudit);

        restStockAuditMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockAuditDTO)))
            .andExpect(status().isBadRequest());

        List<StockAudit> stockAuditList = stockAuditRepository.findAll();
        assertThat(stockAuditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockAuditRepository.findAll().size();
        // set the field null
        stockAudit.setStatus(null);

        // Create the StockAudit, which fails.
        StockAuditDTO stockAuditDTO = stockAuditMapper.toDto(stockAudit);

        restStockAuditMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockAuditDTO)))
            .andExpect(status().isBadRequest());

        List<StockAudit> stockAuditList = stockAuditRepository.findAll();
        assertThat(stockAuditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAuditorLoginIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockAuditRepository.findAll().size();
        // set the field null
        stockAudit.setAuditorLogin(null);

        // Create the StockAudit, which fails.
        StockAuditDTO stockAuditDTO = stockAuditMapper.toDto(stockAudit);

        restStockAuditMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockAuditDTO)))
            .andExpect(status().isBadRequest());

        List<StockAudit> stockAuditList = stockAuditRepository.findAll();
        assertThat(stockAuditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockAuditRepository.findAll().size();
        // set the field null
        stockAudit.setStartedAt(null);

        // Create the StockAudit, which fails.
        StockAuditDTO stockAuditDTO = stockAuditMapper.toDto(stockAudit);

        restStockAuditMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockAuditDTO)))
            .andExpect(status().isBadRequest());

        List<StockAudit> stockAuditList = stockAuditRepository.findAll();
        assertThat(stockAuditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStockAudits() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList
        restStockAuditMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockAudit.getId().intValue())))
            .andExpect(jsonPath("$.[*].tenantId").value(hasItem(DEFAULT_TENANT_ID.intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].theoreticalCount").value(hasItem(DEFAULT_THEORETICAL_COUNT)))
            .andExpect(jsonPath("$.[*].physicalCount").value(hasItem(DEFAULT_PHYSICAL_COUNT)))
            .andExpect(jsonPath("$.[*].discrepancyCount").value(hasItem(DEFAULT_DISCREPANCY_COUNT)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].auditorLogin").value(hasItem(DEFAULT_AUDITOR_LOGIN)))
            .andExpect(jsonPath("$.[*].startedAt").value(hasItem(sameInstant(DEFAULT_STARTED_AT))))
            .andExpect(jsonPath("$.[*].closedAt").value(hasItem(sameInstant(DEFAULT_CLOSED_AT))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStockAuditsWithEagerRelationshipsIsEnabled() throws Exception {
        when(stockAuditServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStockAuditMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(stockAuditServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStockAuditsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(stockAuditServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStockAuditMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(stockAuditRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getStockAudit() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get the stockAudit
        restStockAuditMockMvc
            .perform(get(ENTITY_API_URL_ID, stockAudit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stockAudit.getId().intValue()))
            .andExpect(jsonPath("$.tenantId").value(DEFAULT_TENANT_ID.intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.theoreticalCount").value(DEFAULT_THEORETICAL_COUNT))
            .andExpect(jsonPath("$.physicalCount").value(DEFAULT_PHYSICAL_COUNT))
            .andExpect(jsonPath("$.discrepancyCount").value(DEFAULT_DISCREPANCY_COUNT))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES))
            .andExpect(jsonPath("$.auditorLogin").value(DEFAULT_AUDITOR_LOGIN))
            .andExpect(jsonPath("$.startedAt").value(sameInstant(DEFAULT_STARTED_AT)))
            .andExpect(jsonPath("$.closedAt").value(sameInstant(DEFAULT_CLOSED_AT)));
    }

    @Test
    @Transactional
    void getStockAuditsByIdFiltering() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        Long id = stockAudit.getId();

        defaultStockAuditShouldBeFound("id.equals=" + id);
        defaultStockAuditShouldNotBeFound("id.notEquals=" + id);

        defaultStockAuditShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultStockAuditShouldNotBeFound("id.greaterThan=" + id);

        defaultStockAuditShouldBeFound("id.lessThanOrEqual=" + id);
        defaultStockAuditShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStockAuditsByTenantIdIsEqualToSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where tenantId equals to DEFAULT_TENANT_ID
        defaultStockAuditShouldBeFound("tenantId.equals=" + DEFAULT_TENANT_ID);

        // Get all the stockAuditList where tenantId equals to UPDATED_TENANT_ID
        defaultStockAuditShouldNotBeFound("tenantId.equals=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllStockAuditsByTenantIdIsInShouldWork() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where tenantId in DEFAULT_TENANT_ID or UPDATED_TENANT_ID
        defaultStockAuditShouldBeFound("tenantId.in=" + DEFAULT_TENANT_ID + "," + UPDATED_TENANT_ID);

        // Get all the stockAuditList where tenantId equals to UPDATED_TENANT_ID
        defaultStockAuditShouldNotBeFound("tenantId.in=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllStockAuditsByTenantIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where tenantId is not null
        defaultStockAuditShouldBeFound("tenantId.specified=true");

        // Get all the stockAuditList where tenantId is null
        defaultStockAuditShouldNotBeFound("tenantId.specified=false");
    }

    @Test
    @Transactional
    void getAllStockAuditsByTenantIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where tenantId is greater than or equal to DEFAULT_TENANT_ID
        defaultStockAuditShouldBeFound("tenantId.greaterThanOrEqual=" + DEFAULT_TENANT_ID);

        // Get all the stockAuditList where tenantId is greater than or equal to UPDATED_TENANT_ID
        defaultStockAuditShouldNotBeFound("tenantId.greaterThanOrEqual=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllStockAuditsByTenantIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where tenantId is less than or equal to DEFAULT_TENANT_ID
        defaultStockAuditShouldBeFound("tenantId.lessThanOrEqual=" + DEFAULT_TENANT_ID);

        // Get all the stockAuditList where tenantId is less than or equal to SMALLER_TENANT_ID
        defaultStockAuditShouldNotBeFound("tenantId.lessThanOrEqual=" + SMALLER_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllStockAuditsByTenantIdIsLessThanSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where tenantId is less than DEFAULT_TENANT_ID
        defaultStockAuditShouldNotBeFound("tenantId.lessThan=" + DEFAULT_TENANT_ID);

        // Get all the stockAuditList where tenantId is less than UPDATED_TENANT_ID
        defaultStockAuditShouldBeFound("tenantId.lessThan=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllStockAuditsByTenantIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where tenantId is greater than DEFAULT_TENANT_ID
        defaultStockAuditShouldNotBeFound("tenantId.greaterThan=" + DEFAULT_TENANT_ID);

        // Get all the stockAuditList where tenantId is greater than SMALLER_TENANT_ID
        defaultStockAuditShouldBeFound("tenantId.greaterThan=" + SMALLER_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllStockAuditsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where status equals to DEFAULT_STATUS
        defaultStockAuditShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the stockAuditList where status equals to UPDATED_STATUS
        defaultStockAuditShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllStockAuditsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultStockAuditShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the stockAuditList where status equals to UPDATED_STATUS
        defaultStockAuditShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllStockAuditsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where status is not null
        defaultStockAuditShouldBeFound("status.specified=true");

        // Get all the stockAuditList where status is null
        defaultStockAuditShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllStockAuditsByTheoreticalCountIsEqualToSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where theoreticalCount equals to DEFAULT_THEORETICAL_COUNT
        defaultStockAuditShouldBeFound("theoreticalCount.equals=" + DEFAULT_THEORETICAL_COUNT);

        // Get all the stockAuditList where theoreticalCount equals to UPDATED_THEORETICAL_COUNT
        defaultStockAuditShouldNotBeFound("theoreticalCount.equals=" + UPDATED_THEORETICAL_COUNT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByTheoreticalCountIsInShouldWork() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where theoreticalCount in DEFAULT_THEORETICAL_COUNT or UPDATED_THEORETICAL_COUNT
        defaultStockAuditShouldBeFound("theoreticalCount.in=" + DEFAULT_THEORETICAL_COUNT + "," + UPDATED_THEORETICAL_COUNT);

        // Get all the stockAuditList where theoreticalCount equals to UPDATED_THEORETICAL_COUNT
        defaultStockAuditShouldNotBeFound("theoreticalCount.in=" + UPDATED_THEORETICAL_COUNT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByTheoreticalCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where theoreticalCount is not null
        defaultStockAuditShouldBeFound("theoreticalCount.specified=true");

        // Get all the stockAuditList where theoreticalCount is null
        defaultStockAuditShouldNotBeFound("theoreticalCount.specified=false");
    }

    @Test
    @Transactional
    void getAllStockAuditsByTheoreticalCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where theoreticalCount is greater than or equal to DEFAULT_THEORETICAL_COUNT
        defaultStockAuditShouldBeFound("theoreticalCount.greaterThanOrEqual=" + DEFAULT_THEORETICAL_COUNT);

        // Get all the stockAuditList where theoreticalCount is greater than or equal to UPDATED_THEORETICAL_COUNT
        defaultStockAuditShouldNotBeFound("theoreticalCount.greaterThanOrEqual=" + UPDATED_THEORETICAL_COUNT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByTheoreticalCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where theoreticalCount is less than or equal to DEFAULT_THEORETICAL_COUNT
        defaultStockAuditShouldBeFound("theoreticalCount.lessThanOrEqual=" + DEFAULT_THEORETICAL_COUNT);

        // Get all the stockAuditList where theoreticalCount is less than or equal to SMALLER_THEORETICAL_COUNT
        defaultStockAuditShouldNotBeFound("theoreticalCount.lessThanOrEqual=" + SMALLER_THEORETICAL_COUNT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByTheoreticalCountIsLessThanSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where theoreticalCount is less than DEFAULT_THEORETICAL_COUNT
        defaultStockAuditShouldNotBeFound("theoreticalCount.lessThan=" + DEFAULT_THEORETICAL_COUNT);

        // Get all the stockAuditList where theoreticalCount is less than UPDATED_THEORETICAL_COUNT
        defaultStockAuditShouldBeFound("theoreticalCount.lessThan=" + UPDATED_THEORETICAL_COUNT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByTheoreticalCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where theoreticalCount is greater than DEFAULT_THEORETICAL_COUNT
        defaultStockAuditShouldNotBeFound("theoreticalCount.greaterThan=" + DEFAULT_THEORETICAL_COUNT);

        // Get all the stockAuditList where theoreticalCount is greater than SMALLER_THEORETICAL_COUNT
        defaultStockAuditShouldBeFound("theoreticalCount.greaterThan=" + SMALLER_THEORETICAL_COUNT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByPhysicalCountIsEqualToSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where physicalCount equals to DEFAULT_PHYSICAL_COUNT
        defaultStockAuditShouldBeFound("physicalCount.equals=" + DEFAULT_PHYSICAL_COUNT);

        // Get all the stockAuditList where physicalCount equals to UPDATED_PHYSICAL_COUNT
        defaultStockAuditShouldNotBeFound("physicalCount.equals=" + UPDATED_PHYSICAL_COUNT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByPhysicalCountIsInShouldWork() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where physicalCount in DEFAULT_PHYSICAL_COUNT or UPDATED_PHYSICAL_COUNT
        defaultStockAuditShouldBeFound("physicalCount.in=" + DEFAULT_PHYSICAL_COUNT + "," + UPDATED_PHYSICAL_COUNT);

        // Get all the stockAuditList where physicalCount equals to UPDATED_PHYSICAL_COUNT
        defaultStockAuditShouldNotBeFound("physicalCount.in=" + UPDATED_PHYSICAL_COUNT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByPhysicalCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where physicalCount is not null
        defaultStockAuditShouldBeFound("physicalCount.specified=true");

        // Get all the stockAuditList where physicalCount is null
        defaultStockAuditShouldNotBeFound("physicalCount.specified=false");
    }

    @Test
    @Transactional
    void getAllStockAuditsByPhysicalCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where physicalCount is greater than or equal to DEFAULT_PHYSICAL_COUNT
        defaultStockAuditShouldBeFound("physicalCount.greaterThanOrEqual=" + DEFAULT_PHYSICAL_COUNT);

        // Get all the stockAuditList where physicalCount is greater than or equal to UPDATED_PHYSICAL_COUNT
        defaultStockAuditShouldNotBeFound("physicalCount.greaterThanOrEqual=" + UPDATED_PHYSICAL_COUNT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByPhysicalCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where physicalCount is less than or equal to DEFAULT_PHYSICAL_COUNT
        defaultStockAuditShouldBeFound("physicalCount.lessThanOrEqual=" + DEFAULT_PHYSICAL_COUNT);

        // Get all the stockAuditList where physicalCount is less than or equal to SMALLER_PHYSICAL_COUNT
        defaultStockAuditShouldNotBeFound("physicalCount.lessThanOrEqual=" + SMALLER_PHYSICAL_COUNT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByPhysicalCountIsLessThanSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where physicalCount is less than DEFAULT_PHYSICAL_COUNT
        defaultStockAuditShouldNotBeFound("physicalCount.lessThan=" + DEFAULT_PHYSICAL_COUNT);

        // Get all the stockAuditList where physicalCount is less than UPDATED_PHYSICAL_COUNT
        defaultStockAuditShouldBeFound("physicalCount.lessThan=" + UPDATED_PHYSICAL_COUNT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByPhysicalCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where physicalCount is greater than DEFAULT_PHYSICAL_COUNT
        defaultStockAuditShouldNotBeFound("physicalCount.greaterThan=" + DEFAULT_PHYSICAL_COUNT);

        // Get all the stockAuditList where physicalCount is greater than SMALLER_PHYSICAL_COUNT
        defaultStockAuditShouldBeFound("physicalCount.greaterThan=" + SMALLER_PHYSICAL_COUNT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByDiscrepancyCountIsEqualToSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where discrepancyCount equals to DEFAULT_DISCREPANCY_COUNT
        defaultStockAuditShouldBeFound("discrepancyCount.equals=" + DEFAULT_DISCREPANCY_COUNT);

        // Get all the stockAuditList where discrepancyCount equals to UPDATED_DISCREPANCY_COUNT
        defaultStockAuditShouldNotBeFound("discrepancyCount.equals=" + UPDATED_DISCREPANCY_COUNT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByDiscrepancyCountIsInShouldWork() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where discrepancyCount in DEFAULT_DISCREPANCY_COUNT or UPDATED_DISCREPANCY_COUNT
        defaultStockAuditShouldBeFound("discrepancyCount.in=" + DEFAULT_DISCREPANCY_COUNT + "," + UPDATED_DISCREPANCY_COUNT);

        // Get all the stockAuditList where discrepancyCount equals to UPDATED_DISCREPANCY_COUNT
        defaultStockAuditShouldNotBeFound("discrepancyCount.in=" + UPDATED_DISCREPANCY_COUNT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByDiscrepancyCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where discrepancyCount is not null
        defaultStockAuditShouldBeFound("discrepancyCount.specified=true");

        // Get all the stockAuditList where discrepancyCount is null
        defaultStockAuditShouldNotBeFound("discrepancyCount.specified=false");
    }

    @Test
    @Transactional
    void getAllStockAuditsByDiscrepancyCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where discrepancyCount is greater than or equal to DEFAULT_DISCREPANCY_COUNT
        defaultStockAuditShouldBeFound("discrepancyCount.greaterThanOrEqual=" + DEFAULT_DISCREPANCY_COUNT);

        // Get all the stockAuditList where discrepancyCount is greater than or equal to UPDATED_DISCREPANCY_COUNT
        defaultStockAuditShouldNotBeFound("discrepancyCount.greaterThanOrEqual=" + UPDATED_DISCREPANCY_COUNT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByDiscrepancyCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where discrepancyCount is less than or equal to DEFAULT_DISCREPANCY_COUNT
        defaultStockAuditShouldBeFound("discrepancyCount.lessThanOrEqual=" + DEFAULT_DISCREPANCY_COUNT);

        // Get all the stockAuditList where discrepancyCount is less than or equal to SMALLER_DISCREPANCY_COUNT
        defaultStockAuditShouldNotBeFound("discrepancyCount.lessThanOrEqual=" + SMALLER_DISCREPANCY_COUNT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByDiscrepancyCountIsLessThanSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where discrepancyCount is less than DEFAULT_DISCREPANCY_COUNT
        defaultStockAuditShouldNotBeFound("discrepancyCount.lessThan=" + DEFAULT_DISCREPANCY_COUNT);

        // Get all the stockAuditList where discrepancyCount is less than UPDATED_DISCREPANCY_COUNT
        defaultStockAuditShouldBeFound("discrepancyCount.lessThan=" + UPDATED_DISCREPANCY_COUNT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByDiscrepancyCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where discrepancyCount is greater than DEFAULT_DISCREPANCY_COUNT
        defaultStockAuditShouldNotBeFound("discrepancyCount.greaterThan=" + DEFAULT_DISCREPANCY_COUNT);

        // Get all the stockAuditList where discrepancyCount is greater than SMALLER_DISCREPANCY_COUNT
        defaultStockAuditShouldBeFound("discrepancyCount.greaterThan=" + SMALLER_DISCREPANCY_COUNT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByNotesIsEqualToSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where notes equals to DEFAULT_NOTES
        defaultStockAuditShouldBeFound("notes.equals=" + DEFAULT_NOTES);

        // Get all the stockAuditList where notes equals to UPDATED_NOTES
        defaultStockAuditShouldNotBeFound("notes.equals=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllStockAuditsByNotesIsInShouldWork() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where notes in DEFAULT_NOTES or UPDATED_NOTES
        defaultStockAuditShouldBeFound("notes.in=" + DEFAULT_NOTES + "," + UPDATED_NOTES);

        // Get all the stockAuditList where notes equals to UPDATED_NOTES
        defaultStockAuditShouldNotBeFound("notes.in=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllStockAuditsByNotesIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where notes is not null
        defaultStockAuditShouldBeFound("notes.specified=true");

        // Get all the stockAuditList where notes is null
        defaultStockAuditShouldNotBeFound("notes.specified=false");
    }

    @Test
    @Transactional
    void getAllStockAuditsByNotesContainsSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where notes contains DEFAULT_NOTES
        defaultStockAuditShouldBeFound("notes.contains=" + DEFAULT_NOTES);

        // Get all the stockAuditList where notes contains UPDATED_NOTES
        defaultStockAuditShouldNotBeFound("notes.contains=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllStockAuditsByNotesNotContainsSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where notes does not contain DEFAULT_NOTES
        defaultStockAuditShouldNotBeFound("notes.doesNotContain=" + DEFAULT_NOTES);

        // Get all the stockAuditList where notes does not contain UPDATED_NOTES
        defaultStockAuditShouldBeFound("notes.doesNotContain=" + UPDATED_NOTES);
    }

    @Test
    @Transactional
    void getAllStockAuditsByAuditorLoginIsEqualToSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where auditorLogin equals to DEFAULT_AUDITOR_LOGIN
        defaultStockAuditShouldBeFound("auditorLogin.equals=" + DEFAULT_AUDITOR_LOGIN);

        // Get all the stockAuditList where auditorLogin equals to UPDATED_AUDITOR_LOGIN
        defaultStockAuditShouldNotBeFound("auditorLogin.equals=" + UPDATED_AUDITOR_LOGIN);
    }

    @Test
    @Transactional
    void getAllStockAuditsByAuditorLoginIsInShouldWork() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where auditorLogin in DEFAULT_AUDITOR_LOGIN or UPDATED_AUDITOR_LOGIN
        defaultStockAuditShouldBeFound("auditorLogin.in=" + DEFAULT_AUDITOR_LOGIN + "," + UPDATED_AUDITOR_LOGIN);

        // Get all the stockAuditList where auditorLogin equals to UPDATED_AUDITOR_LOGIN
        defaultStockAuditShouldNotBeFound("auditorLogin.in=" + UPDATED_AUDITOR_LOGIN);
    }

    @Test
    @Transactional
    void getAllStockAuditsByAuditorLoginIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where auditorLogin is not null
        defaultStockAuditShouldBeFound("auditorLogin.specified=true");

        // Get all the stockAuditList where auditorLogin is null
        defaultStockAuditShouldNotBeFound("auditorLogin.specified=false");
    }

    @Test
    @Transactional
    void getAllStockAuditsByAuditorLoginContainsSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where auditorLogin contains DEFAULT_AUDITOR_LOGIN
        defaultStockAuditShouldBeFound("auditorLogin.contains=" + DEFAULT_AUDITOR_LOGIN);

        // Get all the stockAuditList where auditorLogin contains UPDATED_AUDITOR_LOGIN
        defaultStockAuditShouldNotBeFound("auditorLogin.contains=" + UPDATED_AUDITOR_LOGIN);
    }

    @Test
    @Transactional
    void getAllStockAuditsByAuditorLoginNotContainsSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where auditorLogin does not contain DEFAULT_AUDITOR_LOGIN
        defaultStockAuditShouldNotBeFound("auditorLogin.doesNotContain=" + DEFAULT_AUDITOR_LOGIN);

        // Get all the stockAuditList where auditorLogin does not contain UPDATED_AUDITOR_LOGIN
        defaultStockAuditShouldBeFound("auditorLogin.doesNotContain=" + UPDATED_AUDITOR_LOGIN);
    }

    @Test
    @Transactional
    void getAllStockAuditsByStartedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where startedAt equals to DEFAULT_STARTED_AT
        defaultStockAuditShouldBeFound("startedAt.equals=" + DEFAULT_STARTED_AT);

        // Get all the stockAuditList where startedAt equals to UPDATED_STARTED_AT
        defaultStockAuditShouldNotBeFound("startedAt.equals=" + UPDATED_STARTED_AT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByStartedAtIsInShouldWork() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where startedAt in DEFAULT_STARTED_AT or UPDATED_STARTED_AT
        defaultStockAuditShouldBeFound("startedAt.in=" + DEFAULT_STARTED_AT + "," + UPDATED_STARTED_AT);

        // Get all the stockAuditList where startedAt equals to UPDATED_STARTED_AT
        defaultStockAuditShouldNotBeFound("startedAt.in=" + UPDATED_STARTED_AT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByStartedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where startedAt is not null
        defaultStockAuditShouldBeFound("startedAt.specified=true");

        // Get all the stockAuditList where startedAt is null
        defaultStockAuditShouldNotBeFound("startedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllStockAuditsByStartedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where startedAt is greater than or equal to DEFAULT_STARTED_AT
        defaultStockAuditShouldBeFound("startedAt.greaterThanOrEqual=" + DEFAULT_STARTED_AT);

        // Get all the stockAuditList where startedAt is greater than or equal to UPDATED_STARTED_AT
        defaultStockAuditShouldNotBeFound("startedAt.greaterThanOrEqual=" + UPDATED_STARTED_AT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByStartedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where startedAt is less than or equal to DEFAULT_STARTED_AT
        defaultStockAuditShouldBeFound("startedAt.lessThanOrEqual=" + DEFAULT_STARTED_AT);

        // Get all the stockAuditList where startedAt is less than or equal to SMALLER_STARTED_AT
        defaultStockAuditShouldNotBeFound("startedAt.lessThanOrEqual=" + SMALLER_STARTED_AT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByStartedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where startedAt is less than DEFAULT_STARTED_AT
        defaultStockAuditShouldNotBeFound("startedAt.lessThan=" + DEFAULT_STARTED_AT);

        // Get all the stockAuditList where startedAt is less than UPDATED_STARTED_AT
        defaultStockAuditShouldBeFound("startedAt.lessThan=" + UPDATED_STARTED_AT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByStartedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where startedAt is greater than DEFAULT_STARTED_AT
        defaultStockAuditShouldNotBeFound("startedAt.greaterThan=" + DEFAULT_STARTED_AT);

        // Get all the stockAuditList where startedAt is greater than SMALLER_STARTED_AT
        defaultStockAuditShouldBeFound("startedAt.greaterThan=" + SMALLER_STARTED_AT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByClosedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where closedAt equals to DEFAULT_CLOSED_AT
        defaultStockAuditShouldBeFound("closedAt.equals=" + DEFAULT_CLOSED_AT);

        // Get all the stockAuditList where closedAt equals to UPDATED_CLOSED_AT
        defaultStockAuditShouldNotBeFound("closedAt.equals=" + UPDATED_CLOSED_AT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByClosedAtIsInShouldWork() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where closedAt in DEFAULT_CLOSED_AT or UPDATED_CLOSED_AT
        defaultStockAuditShouldBeFound("closedAt.in=" + DEFAULT_CLOSED_AT + "," + UPDATED_CLOSED_AT);

        // Get all the stockAuditList where closedAt equals to UPDATED_CLOSED_AT
        defaultStockAuditShouldNotBeFound("closedAt.in=" + UPDATED_CLOSED_AT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByClosedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where closedAt is not null
        defaultStockAuditShouldBeFound("closedAt.specified=true");

        // Get all the stockAuditList where closedAt is null
        defaultStockAuditShouldNotBeFound("closedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllStockAuditsByClosedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where closedAt is greater than or equal to DEFAULT_CLOSED_AT
        defaultStockAuditShouldBeFound("closedAt.greaterThanOrEqual=" + DEFAULT_CLOSED_AT);

        // Get all the stockAuditList where closedAt is greater than or equal to UPDATED_CLOSED_AT
        defaultStockAuditShouldNotBeFound("closedAt.greaterThanOrEqual=" + UPDATED_CLOSED_AT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByClosedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where closedAt is less than or equal to DEFAULT_CLOSED_AT
        defaultStockAuditShouldBeFound("closedAt.lessThanOrEqual=" + DEFAULT_CLOSED_AT);

        // Get all the stockAuditList where closedAt is less than or equal to SMALLER_CLOSED_AT
        defaultStockAuditShouldNotBeFound("closedAt.lessThanOrEqual=" + SMALLER_CLOSED_AT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByClosedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where closedAt is less than DEFAULT_CLOSED_AT
        defaultStockAuditShouldNotBeFound("closedAt.lessThan=" + DEFAULT_CLOSED_AT);

        // Get all the stockAuditList where closedAt is less than UPDATED_CLOSED_AT
        defaultStockAuditShouldBeFound("closedAt.lessThan=" + UPDATED_CLOSED_AT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByClosedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        // Get all the stockAuditList where closedAt is greater than DEFAULT_CLOSED_AT
        defaultStockAuditShouldNotBeFound("closedAt.greaterThan=" + DEFAULT_CLOSED_AT);

        // Get all the stockAuditList where closedAt is greater than SMALLER_CLOSED_AT
        defaultStockAuditShouldBeFound("closedAt.greaterThan=" + SMALLER_CLOSED_AT);
    }

    @Test
    @Transactional
    void getAllStockAuditsByAuditLinesIsEqualToSomething() throws Exception {
        StockAuditLine auditLines;
        if (TestUtil.findAll(em, StockAuditLine.class).isEmpty()) {
            stockAuditRepository.saveAndFlush(stockAudit);
            auditLines = StockAuditLineResourceIT.createEntity(em);
        } else {
            auditLines = TestUtil.findAll(em, StockAuditLine.class).get(0);
        }
        em.persist(auditLines);
        em.flush();
        stockAudit.addAuditLines(auditLines);
        stockAuditRepository.saveAndFlush(stockAudit);
        Long auditLinesId = auditLines.getId();

        // Get all the stockAuditList where auditLines equals to auditLinesId
        defaultStockAuditShouldBeFound("auditLinesId.equals=" + auditLinesId);

        // Get all the stockAuditList where auditLines equals to (auditLinesId + 1)
        defaultStockAuditShouldNotBeFound("auditLinesId.equals=" + (auditLinesId + 1));
    }

    @Test
    @Transactional
    void getAllStockAuditsByWarehouseIsEqualToSomething() throws Exception {
        Warehouse warehouse;
        if (TestUtil.findAll(em, Warehouse.class).isEmpty()) {
            stockAuditRepository.saveAndFlush(stockAudit);
            warehouse = WarehouseResourceIT.createEntity(em);
        } else {
            warehouse = TestUtil.findAll(em, Warehouse.class).get(0);
        }
        em.persist(warehouse);
        em.flush();
        stockAudit.setWarehouse(warehouse);
        stockAuditRepository.saveAndFlush(stockAudit);
        Long warehouseId = warehouse.getId();

        // Get all the stockAuditList where warehouse equals to warehouseId
        defaultStockAuditShouldBeFound("warehouseId.equals=" + warehouseId);

        // Get all the stockAuditList where warehouse equals to (warehouseId + 1)
        defaultStockAuditShouldNotBeFound("warehouseId.equals=" + (warehouseId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStockAuditShouldBeFound(String filter) throws Exception {
        restStockAuditMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockAudit.getId().intValue())))
            .andExpect(jsonPath("$.[*].tenantId").value(hasItem(DEFAULT_TENANT_ID.intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].theoreticalCount").value(hasItem(DEFAULT_THEORETICAL_COUNT)))
            .andExpect(jsonPath("$.[*].physicalCount").value(hasItem(DEFAULT_PHYSICAL_COUNT)))
            .andExpect(jsonPath("$.[*].discrepancyCount").value(hasItem(DEFAULT_DISCREPANCY_COUNT)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].auditorLogin").value(hasItem(DEFAULT_AUDITOR_LOGIN)))
            .andExpect(jsonPath("$.[*].startedAt").value(hasItem(sameInstant(DEFAULT_STARTED_AT))))
            .andExpect(jsonPath("$.[*].closedAt").value(hasItem(sameInstant(DEFAULT_CLOSED_AT))));

        // Check, that the count call also returns 1
        restStockAuditMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStockAuditShouldNotBeFound(String filter) throws Exception {
        restStockAuditMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStockAuditMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStockAudit() throws Exception {
        // Get the stockAudit
        restStockAuditMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStockAudit() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        int databaseSizeBeforeUpdate = stockAuditRepository.findAll().size();

        // Update the stockAudit
        StockAudit updatedStockAudit = stockAuditRepository.findById(stockAudit.getId()).get();
        // Disconnect from session so that the updates on updatedStockAudit are not directly saved in db
        em.detach(updatedStockAudit);
        updatedStockAudit
            .tenantId(UPDATED_TENANT_ID)
            .status(UPDATED_STATUS)
            .theoreticalCount(UPDATED_THEORETICAL_COUNT)
            .physicalCount(UPDATED_PHYSICAL_COUNT)
            .discrepancyCount(UPDATED_DISCREPANCY_COUNT)
            .notes(UPDATED_NOTES)
            .auditorLogin(UPDATED_AUDITOR_LOGIN)
            .startedAt(UPDATED_STARTED_AT)
            .closedAt(UPDATED_CLOSED_AT);
        StockAuditDTO stockAuditDTO = stockAuditMapper.toDto(updatedStockAudit);

        restStockAuditMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockAuditDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockAuditDTO))
            )
            .andExpect(status().isOk());

        // Validate the StockAudit in the database
        List<StockAudit> stockAuditList = stockAuditRepository.findAll();
        assertThat(stockAuditList).hasSize(databaseSizeBeforeUpdate);
        StockAudit testStockAudit = stockAuditList.get(stockAuditList.size() - 1);
        assertThat(testStockAudit.getTenantId()).isEqualTo(UPDATED_TENANT_ID);
        assertThat(testStockAudit.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testStockAudit.getTheoreticalCount()).isEqualTo(UPDATED_THEORETICAL_COUNT);
        assertThat(testStockAudit.getPhysicalCount()).isEqualTo(UPDATED_PHYSICAL_COUNT);
        assertThat(testStockAudit.getDiscrepancyCount()).isEqualTo(UPDATED_DISCREPANCY_COUNT);
        assertThat(testStockAudit.getNotes()).isEqualTo(UPDATED_NOTES);
        assertThat(testStockAudit.getAuditorLogin()).isEqualTo(UPDATED_AUDITOR_LOGIN);
        assertThat(testStockAudit.getStartedAt()).isEqualTo(UPDATED_STARTED_AT);
        assertThat(testStockAudit.getClosedAt()).isEqualTo(UPDATED_CLOSED_AT);
    }

    @Test
    @Transactional
    void putNonExistingStockAudit() throws Exception {
        int databaseSizeBeforeUpdate = stockAuditRepository.findAll().size();
        stockAudit.setId(count.incrementAndGet());

        // Create the StockAudit
        StockAuditDTO stockAuditDTO = stockAuditMapper.toDto(stockAudit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockAuditMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockAuditDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockAuditDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockAudit in the database
        List<StockAudit> stockAuditList = stockAuditRepository.findAll();
        assertThat(stockAuditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStockAudit() throws Exception {
        int databaseSizeBeforeUpdate = stockAuditRepository.findAll().size();
        stockAudit.setId(count.incrementAndGet());

        // Create the StockAudit
        StockAuditDTO stockAuditDTO = stockAuditMapper.toDto(stockAudit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockAuditMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockAuditDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockAudit in the database
        List<StockAudit> stockAuditList = stockAuditRepository.findAll();
        assertThat(stockAuditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStockAudit() throws Exception {
        int databaseSizeBeforeUpdate = stockAuditRepository.findAll().size();
        stockAudit.setId(count.incrementAndGet());

        // Create the StockAudit
        StockAuditDTO stockAuditDTO = stockAuditMapper.toDto(stockAudit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockAuditMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockAuditDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockAudit in the database
        List<StockAudit> stockAuditList = stockAuditRepository.findAll();
        assertThat(stockAuditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStockAuditWithPatch() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        int databaseSizeBeforeUpdate = stockAuditRepository.findAll().size();

        // Update the stockAudit using partial update
        StockAudit partialUpdatedStockAudit = new StockAudit();
        partialUpdatedStockAudit.setId(stockAudit.getId());

        partialUpdatedStockAudit
            .tenantId(UPDATED_TENANT_ID)
            .status(UPDATED_STATUS)
            .physicalCount(UPDATED_PHYSICAL_COUNT)
            .notes(UPDATED_NOTES)
            .auditorLogin(UPDATED_AUDITOR_LOGIN)
            .startedAt(UPDATED_STARTED_AT);

        restStockAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockAudit.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStockAudit))
            )
            .andExpect(status().isOk());

        // Validate the StockAudit in the database
        List<StockAudit> stockAuditList = stockAuditRepository.findAll();
        assertThat(stockAuditList).hasSize(databaseSizeBeforeUpdate);
        StockAudit testStockAudit = stockAuditList.get(stockAuditList.size() - 1);
        assertThat(testStockAudit.getTenantId()).isEqualTo(UPDATED_TENANT_ID);
        assertThat(testStockAudit.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testStockAudit.getTheoreticalCount()).isEqualTo(DEFAULT_THEORETICAL_COUNT);
        assertThat(testStockAudit.getPhysicalCount()).isEqualTo(UPDATED_PHYSICAL_COUNT);
        assertThat(testStockAudit.getDiscrepancyCount()).isEqualTo(DEFAULT_DISCREPANCY_COUNT);
        assertThat(testStockAudit.getNotes()).isEqualTo(UPDATED_NOTES);
        assertThat(testStockAudit.getAuditorLogin()).isEqualTo(UPDATED_AUDITOR_LOGIN);
        assertThat(testStockAudit.getStartedAt()).isEqualTo(UPDATED_STARTED_AT);
        assertThat(testStockAudit.getClosedAt()).isEqualTo(DEFAULT_CLOSED_AT);
    }

    @Test
    @Transactional
    void fullUpdateStockAuditWithPatch() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        int databaseSizeBeforeUpdate = stockAuditRepository.findAll().size();

        // Update the stockAudit using partial update
        StockAudit partialUpdatedStockAudit = new StockAudit();
        partialUpdatedStockAudit.setId(stockAudit.getId());

        partialUpdatedStockAudit
            .tenantId(UPDATED_TENANT_ID)
            .status(UPDATED_STATUS)
            .theoreticalCount(UPDATED_THEORETICAL_COUNT)
            .physicalCount(UPDATED_PHYSICAL_COUNT)
            .discrepancyCount(UPDATED_DISCREPANCY_COUNT)
            .notes(UPDATED_NOTES)
            .auditorLogin(UPDATED_AUDITOR_LOGIN)
            .startedAt(UPDATED_STARTED_AT)
            .closedAt(UPDATED_CLOSED_AT);

        restStockAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockAudit.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStockAudit))
            )
            .andExpect(status().isOk());

        // Validate the StockAudit in the database
        List<StockAudit> stockAuditList = stockAuditRepository.findAll();
        assertThat(stockAuditList).hasSize(databaseSizeBeforeUpdate);
        StockAudit testStockAudit = stockAuditList.get(stockAuditList.size() - 1);
        assertThat(testStockAudit.getTenantId()).isEqualTo(UPDATED_TENANT_ID);
        assertThat(testStockAudit.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testStockAudit.getTheoreticalCount()).isEqualTo(UPDATED_THEORETICAL_COUNT);
        assertThat(testStockAudit.getPhysicalCount()).isEqualTo(UPDATED_PHYSICAL_COUNT);
        assertThat(testStockAudit.getDiscrepancyCount()).isEqualTo(UPDATED_DISCREPANCY_COUNT);
        assertThat(testStockAudit.getNotes()).isEqualTo(UPDATED_NOTES);
        assertThat(testStockAudit.getAuditorLogin()).isEqualTo(UPDATED_AUDITOR_LOGIN);
        assertThat(testStockAudit.getStartedAt()).isEqualTo(UPDATED_STARTED_AT);
        assertThat(testStockAudit.getClosedAt()).isEqualTo(UPDATED_CLOSED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingStockAudit() throws Exception {
        int databaseSizeBeforeUpdate = stockAuditRepository.findAll().size();
        stockAudit.setId(count.incrementAndGet());

        // Create the StockAudit
        StockAuditDTO stockAuditDTO = stockAuditMapper.toDto(stockAudit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stockAuditDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockAuditDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockAudit in the database
        List<StockAudit> stockAuditList = stockAuditRepository.findAll();
        assertThat(stockAuditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStockAudit() throws Exception {
        int databaseSizeBeforeUpdate = stockAuditRepository.findAll().size();
        stockAudit.setId(count.incrementAndGet());

        // Create the StockAudit
        StockAuditDTO stockAuditDTO = stockAuditMapper.toDto(stockAudit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockAuditDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockAudit in the database
        List<StockAudit> stockAuditList = stockAuditRepository.findAll();
        assertThat(stockAuditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStockAudit() throws Exception {
        int databaseSizeBeforeUpdate = stockAuditRepository.findAll().size();
        stockAudit.setId(count.incrementAndGet());

        // Create the StockAudit
        StockAuditDTO stockAuditDTO = stockAuditMapper.toDto(stockAudit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockAuditMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(stockAuditDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockAudit in the database
        List<StockAudit> stockAuditList = stockAuditRepository.findAll();
        assertThat(stockAuditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStockAudit() throws Exception {
        // Initialize the database
        stockAuditRepository.saveAndFlush(stockAudit);

        int databaseSizeBeforeDelete = stockAuditRepository.findAll().size();

        // Delete the stockAudit
        restStockAuditMockMvc
            .perform(delete(ENTITY_API_URL_ID, stockAudit.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<StockAudit> stockAuditList = stockAuditRepository.findAll();
        assertThat(stockAuditList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
