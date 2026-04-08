package com.tunisales.inventory.web.rest;

import static com.tunisales.inventory.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tunisales.inventory.IntegrationTest;
import com.tunisales.inventory.domain.StockItem;
import com.tunisales.inventory.domain.Swap;
import com.tunisales.inventory.domain.enumeration.SwapStatus;
import com.tunisales.inventory.repository.SwapRepository;
import com.tunisales.inventory.service.SwapService;
import com.tunisales.inventory.service.criteria.SwapCriteria;
import com.tunisales.inventory.service.dto.SwapDTO;
import com.tunisales.inventory.service.mapper.SwapMapper;
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
 * Integration tests for the {@link SwapResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SwapResourceIT {

    private static final Long DEFAULT_TENANT_ID = 1L;
    private static final Long UPDATED_TENANT_ID = 2L;
    private static final Long SMALLER_TENANT_ID = 1L - 1L;

    private static final Long DEFAULT_CLIENT_ID = 1L;
    private static final Long UPDATED_CLIENT_ID = 2L;
    private static final Long SMALLER_CLIENT_ID = 1L - 1L;

    private static final String DEFAULT_CLIENT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CLIENT_NAME = "BBBBBBBBBB";

    private static final SwapStatus DEFAULT_STATUS = SwapStatus.PENDING;
    private static final SwapStatus UPDATED_STATUS = SwapStatus.IN_PROGRESS;

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_RESOLVED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_RESOLVED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_RESOLVED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/swaps";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SwapRepository swapRepository;

    @Mock
    private SwapRepository swapRepositoryMock;

    @Autowired
    private SwapMapper swapMapper;

    @Mock
    private SwapService swapServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSwapMockMvc;

    private Swap swap;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Swap createEntity(EntityManager em) {
        Swap swap = new Swap()
            .tenantId(DEFAULT_TENANT_ID)
            .clientId(DEFAULT_CLIENT_ID)
            .clientName(DEFAULT_CLIENT_NAME)
            .status(DEFAULT_STATUS)
            .reason(DEFAULT_REASON)
            .createdAt(DEFAULT_CREATED_AT)
            .resolvedAt(DEFAULT_RESOLVED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        // Add required entity
        StockItem stockItem;
        if (TestUtil.findAll(em, StockItem.class).isEmpty()) {
            stockItem = StockItemResourceIT.createEntity(em);
            em.persist(stockItem);
            em.flush();
        } else {
            stockItem = TestUtil.findAll(em, StockItem.class).get(0);
        }
        swap.setOutgoingItem(stockItem);
        return swap;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Swap createUpdatedEntity(EntityManager em) {
        Swap swap = new Swap()
            .tenantId(UPDATED_TENANT_ID)
            .clientId(UPDATED_CLIENT_ID)
            .clientName(UPDATED_CLIENT_NAME)
            .status(UPDATED_STATUS)
            .reason(UPDATED_REASON)
            .createdAt(UPDATED_CREATED_AT)
            .resolvedAt(UPDATED_RESOLVED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        // Add required entity
        StockItem stockItem;
        if (TestUtil.findAll(em, StockItem.class).isEmpty()) {
            stockItem = StockItemResourceIT.createUpdatedEntity(em);
            em.persist(stockItem);
            em.flush();
        } else {
            stockItem = TestUtil.findAll(em, StockItem.class).get(0);
        }
        swap.setOutgoingItem(stockItem);
        return swap;
    }

    @BeforeEach
    public void initTest() {
        swap = createEntity(em);
    }

    @Test
    @Transactional
    void createSwap() throws Exception {
        int databaseSizeBeforeCreate = swapRepository.findAll().size();
        // Create the Swap
        SwapDTO swapDTO = swapMapper.toDto(swap);
        restSwapMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(swapDTO)))
            .andExpect(status().isCreated());

        // Validate the Swap in the database
        List<Swap> swapList = swapRepository.findAll();
        assertThat(swapList).hasSize(databaseSizeBeforeCreate + 1);
        Swap testSwap = swapList.get(swapList.size() - 1);
        assertThat(testSwap.getTenantId()).isEqualTo(DEFAULT_TENANT_ID);
        assertThat(testSwap.getClientId()).isEqualTo(DEFAULT_CLIENT_ID);
        assertThat(testSwap.getClientName()).isEqualTo(DEFAULT_CLIENT_NAME);
        assertThat(testSwap.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testSwap.getReason()).isEqualTo(DEFAULT_REASON);
        assertThat(testSwap.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testSwap.getResolvedAt()).isEqualTo(DEFAULT_RESOLVED_AT);
        assertThat(testSwap.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    @Transactional
    void createSwapWithExistingId() throws Exception {
        // Create the Swap with an existing ID
        swap.setId(1L);
        SwapDTO swapDTO = swapMapper.toDto(swap);

        int databaseSizeBeforeCreate = swapRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSwapMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(swapDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Swap in the database
        List<Swap> swapList = swapRepository.findAll();
        assertThat(swapList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTenantIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = swapRepository.findAll().size();
        // set the field null
        swap.setTenantId(null);

        // Create the Swap, which fails.
        SwapDTO swapDTO = swapMapper.toDto(swap);

        restSwapMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(swapDTO)))
            .andExpect(status().isBadRequest());

        List<Swap> swapList = swapRepository.findAll();
        assertThat(swapList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkClientIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = swapRepository.findAll().size();
        // set the field null
        swap.setClientId(null);

        // Create the Swap, which fails.
        SwapDTO swapDTO = swapMapper.toDto(swap);

        restSwapMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(swapDTO)))
            .andExpect(status().isBadRequest());

        List<Swap> swapList = swapRepository.findAll();
        assertThat(swapList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = swapRepository.findAll().size();
        // set the field null
        swap.setStatus(null);

        // Create the Swap, which fails.
        SwapDTO swapDTO = swapMapper.toDto(swap);

        restSwapMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(swapDTO)))
            .andExpect(status().isBadRequest());

        List<Swap> swapList = swapRepository.findAll();
        assertThat(swapList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = swapRepository.findAll().size();
        // set the field null
        swap.setCreatedAt(null);

        // Create the Swap, which fails.
        SwapDTO swapDTO = swapMapper.toDto(swap);

        restSwapMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(swapDTO)))
            .andExpect(status().isBadRequest());

        List<Swap> swapList = swapRepository.findAll();
        assertThat(swapList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSwaps() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList
        restSwapMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(swap.getId().intValue())))
            .andExpect(jsonPath("$.[*].tenantId").value(hasItem(DEFAULT_TENANT_ID.intValue())))
            .andExpect(jsonPath("$.[*].clientId").value(hasItem(DEFAULT_CLIENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].clientName").value(hasItem(DEFAULT_CLIENT_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].resolvedAt").value(hasItem(sameInstant(DEFAULT_RESOLVED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSwapsWithEagerRelationshipsIsEnabled() throws Exception {
        when(swapServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSwapMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(swapServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSwapsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(swapServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSwapMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(swapRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSwap() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get the swap
        restSwapMockMvc
            .perform(get(ENTITY_API_URL_ID, swap.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(swap.getId().intValue()))
            .andExpect(jsonPath("$.tenantId").value(DEFAULT_TENANT_ID.intValue()))
            .andExpect(jsonPath("$.clientId").value(DEFAULT_CLIENT_ID.intValue()))
            .andExpect(jsonPath("$.clientName").value(DEFAULT_CLIENT_NAME))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.reason").value(DEFAULT_REASON))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)))
            .andExpect(jsonPath("$.resolvedAt").value(sameInstant(DEFAULT_RESOLVED_AT)))
            .andExpect(jsonPath("$.updatedAt").value(sameInstant(DEFAULT_UPDATED_AT)));
    }

    @Test
    @Transactional
    void getSwapsByIdFiltering() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        Long id = swap.getId();

        defaultSwapShouldBeFound("id.equals=" + id);
        defaultSwapShouldNotBeFound("id.notEquals=" + id);

        defaultSwapShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSwapShouldNotBeFound("id.greaterThan=" + id);

        defaultSwapShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSwapShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSwapsByTenantIdIsEqualToSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where tenantId equals to DEFAULT_TENANT_ID
        defaultSwapShouldBeFound("tenantId.equals=" + DEFAULT_TENANT_ID);

        // Get all the swapList where tenantId equals to UPDATED_TENANT_ID
        defaultSwapShouldNotBeFound("tenantId.equals=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllSwapsByTenantIdIsInShouldWork() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where tenantId in DEFAULT_TENANT_ID or UPDATED_TENANT_ID
        defaultSwapShouldBeFound("tenantId.in=" + DEFAULT_TENANT_ID + "," + UPDATED_TENANT_ID);

        // Get all the swapList where tenantId equals to UPDATED_TENANT_ID
        defaultSwapShouldNotBeFound("tenantId.in=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllSwapsByTenantIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where tenantId is not null
        defaultSwapShouldBeFound("tenantId.specified=true");

        // Get all the swapList where tenantId is null
        defaultSwapShouldNotBeFound("tenantId.specified=false");
    }

    @Test
    @Transactional
    void getAllSwapsByTenantIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where tenantId is greater than or equal to DEFAULT_TENANT_ID
        defaultSwapShouldBeFound("tenantId.greaterThanOrEqual=" + DEFAULT_TENANT_ID);

        // Get all the swapList where tenantId is greater than or equal to UPDATED_TENANT_ID
        defaultSwapShouldNotBeFound("tenantId.greaterThanOrEqual=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllSwapsByTenantIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where tenantId is less than or equal to DEFAULT_TENANT_ID
        defaultSwapShouldBeFound("tenantId.lessThanOrEqual=" + DEFAULT_TENANT_ID);

        // Get all the swapList where tenantId is less than or equal to SMALLER_TENANT_ID
        defaultSwapShouldNotBeFound("tenantId.lessThanOrEqual=" + SMALLER_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllSwapsByTenantIdIsLessThanSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where tenantId is less than DEFAULT_TENANT_ID
        defaultSwapShouldNotBeFound("tenantId.lessThan=" + DEFAULT_TENANT_ID);

        // Get all the swapList where tenantId is less than UPDATED_TENANT_ID
        defaultSwapShouldBeFound("tenantId.lessThan=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllSwapsByTenantIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where tenantId is greater than DEFAULT_TENANT_ID
        defaultSwapShouldNotBeFound("tenantId.greaterThan=" + DEFAULT_TENANT_ID);

        // Get all the swapList where tenantId is greater than SMALLER_TENANT_ID
        defaultSwapShouldBeFound("tenantId.greaterThan=" + SMALLER_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllSwapsByClientIdIsEqualToSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where clientId equals to DEFAULT_CLIENT_ID
        defaultSwapShouldBeFound("clientId.equals=" + DEFAULT_CLIENT_ID);

        // Get all the swapList where clientId equals to UPDATED_CLIENT_ID
        defaultSwapShouldNotBeFound("clientId.equals=" + UPDATED_CLIENT_ID);
    }

    @Test
    @Transactional
    void getAllSwapsByClientIdIsInShouldWork() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where clientId in DEFAULT_CLIENT_ID or UPDATED_CLIENT_ID
        defaultSwapShouldBeFound("clientId.in=" + DEFAULT_CLIENT_ID + "," + UPDATED_CLIENT_ID);

        // Get all the swapList where clientId equals to UPDATED_CLIENT_ID
        defaultSwapShouldNotBeFound("clientId.in=" + UPDATED_CLIENT_ID);
    }

    @Test
    @Transactional
    void getAllSwapsByClientIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where clientId is not null
        defaultSwapShouldBeFound("clientId.specified=true");

        // Get all the swapList where clientId is null
        defaultSwapShouldNotBeFound("clientId.specified=false");
    }

    @Test
    @Transactional
    void getAllSwapsByClientIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where clientId is greater than or equal to DEFAULT_CLIENT_ID
        defaultSwapShouldBeFound("clientId.greaterThanOrEqual=" + DEFAULT_CLIENT_ID);

        // Get all the swapList where clientId is greater than or equal to UPDATED_CLIENT_ID
        defaultSwapShouldNotBeFound("clientId.greaterThanOrEqual=" + UPDATED_CLIENT_ID);
    }

    @Test
    @Transactional
    void getAllSwapsByClientIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where clientId is less than or equal to DEFAULT_CLIENT_ID
        defaultSwapShouldBeFound("clientId.lessThanOrEqual=" + DEFAULT_CLIENT_ID);

        // Get all the swapList where clientId is less than or equal to SMALLER_CLIENT_ID
        defaultSwapShouldNotBeFound("clientId.lessThanOrEqual=" + SMALLER_CLIENT_ID);
    }

    @Test
    @Transactional
    void getAllSwapsByClientIdIsLessThanSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where clientId is less than DEFAULT_CLIENT_ID
        defaultSwapShouldNotBeFound("clientId.lessThan=" + DEFAULT_CLIENT_ID);

        // Get all the swapList where clientId is less than UPDATED_CLIENT_ID
        defaultSwapShouldBeFound("clientId.lessThan=" + UPDATED_CLIENT_ID);
    }

    @Test
    @Transactional
    void getAllSwapsByClientIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where clientId is greater than DEFAULT_CLIENT_ID
        defaultSwapShouldNotBeFound("clientId.greaterThan=" + DEFAULT_CLIENT_ID);

        // Get all the swapList where clientId is greater than SMALLER_CLIENT_ID
        defaultSwapShouldBeFound("clientId.greaterThan=" + SMALLER_CLIENT_ID);
    }

    @Test
    @Transactional
    void getAllSwapsByClientNameIsEqualToSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where clientName equals to DEFAULT_CLIENT_NAME
        defaultSwapShouldBeFound("clientName.equals=" + DEFAULT_CLIENT_NAME);

        // Get all the swapList where clientName equals to UPDATED_CLIENT_NAME
        defaultSwapShouldNotBeFound("clientName.equals=" + UPDATED_CLIENT_NAME);
    }

    @Test
    @Transactional
    void getAllSwapsByClientNameIsInShouldWork() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where clientName in DEFAULT_CLIENT_NAME or UPDATED_CLIENT_NAME
        defaultSwapShouldBeFound("clientName.in=" + DEFAULT_CLIENT_NAME + "," + UPDATED_CLIENT_NAME);

        // Get all the swapList where clientName equals to UPDATED_CLIENT_NAME
        defaultSwapShouldNotBeFound("clientName.in=" + UPDATED_CLIENT_NAME);
    }

    @Test
    @Transactional
    void getAllSwapsByClientNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where clientName is not null
        defaultSwapShouldBeFound("clientName.specified=true");

        // Get all the swapList where clientName is null
        defaultSwapShouldNotBeFound("clientName.specified=false");
    }

    @Test
    @Transactional
    void getAllSwapsByClientNameContainsSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where clientName contains DEFAULT_CLIENT_NAME
        defaultSwapShouldBeFound("clientName.contains=" + DEFAULT_CLIENT_NAME);

        // Get all the swapList where clientName contains UPDATED_CLIENT_NAME
        defaultSwapShouldNotBeFound("clientName.contains=" + UPDATED_CLIENT_NAME);
    }

    @Test
    @Transactional
    void getAllSwapsByClientNameNotContainsSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where clientName does not contain DEFAULT_CLIENT_NAME
        defaultSwapShouldNotBeFound("clientName.doesNotContain=" + DEFAULT_CLIENT_NAME);

        // Get all the swapList where clientName does not contain UPDATED_CLIENT_NAME
        defaultSwapShouldBeFound("clientName.doesNotContain=" + UPDATED_CLIENT_NAME);
    }

    @Test
    @Transactional
    void getAllSwapsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where status equals to DEFAULT_STATUS
        defaultSwapShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the swapList where status equals to UPDATED_STATUS
        defaultSwapShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllSwapsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultSwapShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the swapList where status equals to UPDATED_STATUS
        defaultSwapShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllSwapsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where status is not null
        defaultSwapShouldBeFound("status.specified=true");

        // Get all the swapList where status is null
        defaultSwapShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllSwapsByReasonIsEqualToSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where reason equals to DEFAULT_REASON
        defaultSwapShouldBeFound("reason.equals=" + DEFAULT_REASON);

        // Get all the swapList where reason equals to UPDATED_REASON
        defaultSwapShouldNotBeFound("reason.equals=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllSwapsByReasonIsInShouldWork() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where reason in DEFAULT_REASON or UPDATED_REASON
        defaultSwapShouldBeFound("reason.in=" + DEFAULT_REASON + "," + UPDATED_REASON);

        // Get all the swapList where reason equals to UPDATED_REASON
        defaultSwapShouldNotBeFound("reason.in=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllSwapsByReasonIsNullOrNotNull() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where reason is not null
        defaultSwapShouldBeFound("reason.specified=true");

        // Get all the swapList where reason is null
        defaultSwapShouldNotBeFound("reason.specified=false");
    }

    @Test
    @Transactional
    void getAllSwapsByReasonContainsSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where reason contains DEFAULT_REASON
        defaultSwapShouldBeFound("reason.contains=" + DEFAULT_REASON);

        // Get all the swapList where reason contains UPDATED_REASON
        defaultSwapShouldNotBeFound("reason.contains=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllSwapsByReasonNotContainsSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where reason does not contain DEFAULT_REASON
        defaultSwapShouldNotBeFound("reason.doesNotContain=" + DEFAULT_REASON);

        // Get all the swapList where reason does not contain UPDATED_REASON
        defaultSwapShouldBeFound("reason.doesNotContain=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllSwapsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where createdAt equals to DEFAULT_CREATED_AT
        defaultSwapShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the swapList where createdAt equals to UPDATED_CREATED_AT
        defaultSwapShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSwapsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultSwapShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the swapList where createdAt equals to UPDATED_CREATED_AT
        defaultSwapShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSwapsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where createdAt is not null
        defaultSwapShouldBeFound("createdAt.specified=true");

        // Get all the swapList where createdAt is null
        defaultSwapShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllSwapsByCreatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where createdAt is greater than or equal to DEFAULT_CREATED_AT
        defaultSwapShouldBeFound("createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the swapList where createdAt is greater than or equal to UPDATED_CREATED_AT
        defaultSwapShouldNotBeFound("createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSwapsByCreatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where createdAt is less than or equal to DEFAULT_CREATED_AT
        defaultSwapShouldBeFound("createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the swapList where createdAt is less than or equal to SMALLER_CREATED_AT
        defaultSwapShouldNotBeFound("createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSwapsByCreatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where createdAt is less than DEFAULT_CREATED_AT
        defaultSwapShouldNotBeFound("createdAt.lessThan=" + DEFAULT_CREATED_AT);

        // Get all the swapList where createdAt is less than UPDATED_CREATED_AT
        defaultSwapShouldBeFound("createdAt.lessThan=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSwapsByCreatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where createdAt is greater than DEFAULT_CREATED_AT
        defaultSwapShouldNotBeFound("createdAt.greaterThan=" + DEFAULT_CREATED_AT);

        // Get all the swapList where createdAt is greater than SMALLER_CREATED_AT
        defaultSwapShouldBeFound("createdAt.greaterThan=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllSwapsByResolvedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where resolvedAt equals to DEFAULT_RESOLVED_AT
        defaultSwapShouldBeFound("resolvedAt.equals=" + DEFAULT_RESOLVED_AT);

        // Get all the swapList where resolvedAt equals to UPDATED_RESOLVED_AT
        defaultSwapShouldNotBeFound("resolvedAt.equals=" + UPDATED_RESOLVED_AT);
    }

    @Test
    @Transactional
    void getAllSwapsByResolvedAtIsInShouldWork() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where resolvedAt in DEFAULT_RESOLVED_AT or UPDATED_RESOLVED_AT
        defaultSwapShouldBeFound("resolvedAt.in=" + DEFAULT_RESOLVED_AT + "," + UPDATED_RESOLVED_AT);

        // Get all the swapList where resolvedAt equals to UPDATED_RESOLVED_AT
        defaultSwapShouldNotBeFound("resolvedAt.in=" + UPDATED_RESOLVED_AT);
    }

    @Test
    @Transactional
    void getAllSwapsByResolvedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where resolvedAt is not null
        defaultSwapShouldBeFound("resolvedAt.specified=true");

        // Get all the swapList where resolvedAt is null
        defaultSwapShouldNotBeFound("resolvedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllSwapsByResolvedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where resolvedAt is greater than or equal to DEFAULT_RESOLVED_AT
        defaultSwapShouldBeFound("resolvedAt.greaterThanOrEqual=" + DEFAULT_RESOLVED_AT);

        // Get all the swapList where resolvedAt is greater than or equal to UPDATED_RESOLVED_AT
        defaultSwapShouldNotBeFound("resolvedAt.greaterThanOrEqual=" + UPDATED_RESOLVED_AT);
    }

    @Test
    @Transactional
    void getAllSwapsByResolvedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where resolvedAt is less than or equal to DEFAULT_RESOLVED_AT
        defaultSwapShouldBeFound("resolvedAt.lessThanOrEqual=" + DEFAULT_RESOLVED_AT);

        // Get all the swapList where resolvedAt is less than or equal to SMALLER_RESOLVED_AT
        defaultSwapShouldNotBeFound("resolvedAt.lessThanOrEqual=" + SMALLER_RESOLVED_AT);
    }

    @Test
    @Transactional
    void getAllSwapsByResolvedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where resolvedAt is less than DEFAULT_RESOLVED_AT
        defaultSwapShouldNotBeFound("resolvedAt.lessThan=" + DEFAULT_RESOLVED_AT);

        // Get all the swapList where resolvedAt is less than UPDATED_RESOLVED_AT
        defaultSwapShouldBeFound("resolvedAt.lessThan=" + UPDATED_RESOLVED_AT);
    }

    @Test
    @Transactional
    void getAllSwapsByResolvedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where resolvedAt is greater than DEFAULT_RESOLVED_AT
        defaultSwapShouldNotBeFound("resolvedAt.greaterThan=" + DEFAULT_RESOLVED_AT);

        // Get all the swapList where resolvedAt is greater than SMALLER_RESOLVED_AT
        defaultSwapShouldBeFound("resolvedAt.greaterThan=" + SMALLER_RESOLVED_AT);
    }

    @Test
    @Transactional
    void getAllSwapsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where updatedAt equals to DEFAULT_UPDATED_AT
        defaultSwapShouldBeFound("updatedAt.equals=" + DEFAULT_UPDATED_AT);

        // Get all the swapList where updatedAt equals to UPDATED_UPDATED_AT
        defaultSwapShouldNotBeFound("updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllSwapsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where updatedAt in DEFAULT_UPDATED_AT or UPDATED_UPDATED_AT
        defaultSwapShouldBeFound("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT);

        // Get all the swapList where updatedAt equals to UPDATED_UPDATED_AT
        defaultSwapShouldNotBeFound("updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllSwapsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where updatedAt is not null
        defaultSwapShouldBeFound("updatedAt.specified=true");

        // Get all the swapList where updatedAt is null
        defaultSwapShouldNotBeFound("updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllSwapsByUpdatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where updatedAt is greater than or equal to DEFAULT_UPDATED_AT
        defaultSwapShouldBeFound("updatedAt.greaterThanOrEqual=" + DEFAULT_UPDATED_AT);

        // Get all the swapList where updatedAt is greater than or equal to UPDATED_UPDATED_AT
        defaultSwapShouldNotBeFound("updatedAt.greaterThanOrEqual=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllSwapsByUpdatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where updatedAt is less than or equal to DEFAULT_UPDATED_AT
        defaultSwapShouldBeFound("updatedAt.lessThanOrEqual=" + DEFAULT_UPDATED_AT);

        // Get all the swapList where updatedAt is less than or equal to SMALLER_UPDATED_AT
        defaultSwapShouldNotBeFound("updatedAt.lessThanOrEqual=" + SMALLER_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllSwapsByUpdatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where updatedAt is less than DEFAULT_UPDATED_AT
        defaultSwapShouldNotBeFound("updatedAt.lessThan=" + DEFAULT_UPDATED_AT);

        // Get all the swapList where updatedAt is less than UPDATED_UPDATED_AT
        defaultSwapShouldBeFound("updatedAt.lessThan=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllSwapsByUpdatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        // Get all the swapList where updatedAt is greater than DEFAULT_UPDATED_AT
        defaultSwapShouldNotBeFound("updatedAt.greaterThan=" + DEFAULT_UPDATED_AT);

        // Get all the swapList where updatedAt is greater than SMALLER_UPDATED_AT
        defaultSwapShouldBeFound("updatedAt.greaterThan=" + SMALLER_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllSwapsByOutgoingItemIsEqualToSomething() throws Exception {
        StockItem outgoingItem;
        if (TestUtil.findAll(em, StockItem.class).isEmpty()) {
            swapRepository.saveAndFlush(swap);
            outgoingItem = StockItemResourceIT.createEntity(em);
        } else {
            outgoingItem = TestUtil.findAll(em, StockItem.class).get(0);
        }
        em.persist(outgoingItem);
        em.flush();
        swap.setOutgoingItem(outgoingItem);
        swapRepository.saveAndFlush(swap);
        Long outgoingItemId = outgoingItem.getId();

        // Get all the swapList where outgoingItem equals to outgoingItemId
        defaultSwapShouldBeFound("outgoingItemId.equals=" + outgoingItemId);

        // Get all the swapList where outgoingItem equals to (outgoingItemId + 1)
        defaultSwapShouldNotBeFound("outgoingItemId.equals=" + (outgoingItemId + 1));
    }

    @Test
    @Transactional
    void getAllSwapsByIncomingItemIsEqualToSomething() throws Exception {
        StockItem incomingItem;
        if (TestUtil.findAll(em, StockItem.class).isEmpty()) {
            swapRepository.saveAndFlush(swap);
            incomingItem = StockItemResourceIT.createEntity(em);
        } else {
            incomingItem = TestUtil.findAll(em, StockItem.class).get(0);
        }
        em.persist(incomingItem);
        em.flush();
        swap.setIncomingItem(incomingItem);
        swapRepository.saveAndFlush(swap);
        Long incomingItemId = incomingItem.getId();

        // Get all the swapList where incomingItem equals to incomingItemId
        defaultSwapShouldBeFound("incomingItemId.equals=" + incomingItemId);

        // Get all the swapList where incomingItem equals to (incomingItemId + 1)
        defaultSwapShouldNotBeFound("incomingItemId.equals=" + (incomingItemId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSwapShouldBeFound(String filter) throws Exception {
        restSwapMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(swap.getId().intValue())))
            .andExpect(jsonPath("$.[*].tenantId").value(hasItem(DEFAULT_TENANT_ID.intValue())))
            .andExpect(jsonPath("$.[*].clientId").value(hasItem(DEFAULT_CLIENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].clientName").value(hasItem(DEFAULT_CLIENT_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].resolvedAt").value(hasItem(sameInstant(DEFAULT_RESOLVED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))));

        // Check, that the count call also returns 1
        restSwapMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSwapShouldNotBeFound(String filter) throws Exception {
        restSwapMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSwapMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSwap() throws Exception {
        // Get the swap
        restSwapMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSwap() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        int databaseSizeBeforeUpdate = swapRepository.findAll().size();

        // Update the swap
        Swap updatedSwap = swapRepository.findById(swap.getId()).get();
        // Disconnect from session so that the updates on updatedSwap are not directly saved in db
        em.detach(updatedSwap);
        updatedSwap
            .tenantId(UPDATED_TENANT_ID)
            .clientId(UPDATED_CLIENT_ID)
            .clientName(UPDATED_CLIENT_NAME)
            .status(UPDATED_STATUS)
            .reason(UPDATED_REASON)
            .createdAt(UPDATED_CREATED_AT)
            .resolvedAt(UPDATED_RESOLVED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        SwapDTO swapDTO = swapMapper.toDto(updatedSwap);

        restSwapMockMvc
            .perform(
                put(ENTITY_API_URL_ID, swapDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(swapDTO))
            )
            .andExpect(status().isOk());

        // Validate the Swap in the database
        List<Swap> swapList = swapRepository.findAll();
        assertThat(swapList).hasSize(databaseSizeBeforeUpdate);
        Swap testSwap = swapList.get(swapList.size() - 1);
        assertThat(testSwap.getTenantId()).isEqualTo(UPDATED_TENANT_ID);
        assertThat(testSwap.getClientId()).isEqualTo(UPDATED_CLIENT_ID);
        assertThat(testSwap.getClientName()).isEqualTo(UPDATED_CLIENT_NAME);
        assertThat(testSwap.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testSwap.getReason()).isEqualTo(UPDATED_REASON);
        assertThat(testSwap.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testSwap.getResolvedAt()).isEqualTo(UPDATED_RESOLVED_AT);
        assertThat(testSwap.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingSwap() throws Exception {
        int databaseSizeBeforeUpdate = swapRepository.findAll().size();
        swap.setId(count.incrementAndGet());

        // Create the Swap
        SwapDTO swapDTO = swapMapper.toDto(swap);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSwapMockMvc
            .perform(
                put(ENTITY_API_URL_ID, swapDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(swapDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Swap in the database
        List<Swap> swapList = swapRepository.findAll();
        assertThat(swapList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSwap() throws Exception {
        int databaseSizeBeforeUpdate = swapRepository.findAll().size();
        swap.setId(count.incrementAndGet());

        // Create the Swap
        SwapDTO swapDTO = swapMapper.toDto(swap);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSwapMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(swapDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Swap in the database
        List<Swap> swapList = swapRepository.findAll();
        assertThat(swapList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSwap() throws Exception {
        int databaseSizeBeforeUpdate = swapRepository.findAll().size();
        swap.setId(count.incrementAndGet());

        // Create the Swap
        SwapDTO swapDTO = swapMapper.toDto(swap);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSwapMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(swapDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Swap in the database
        List<Swap> swapList = swapRepository.findAll();
        assertThat(swapList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSwapWithPatch() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        int databaseSizeBeforeUpdate = swapRepository.findAll().size();

        // Update the swap using partial update
        Swap partialUpdatedSwap = new Swap();
        partialUpdatedSwap.setId(swap.getId());

        partialUpdatedSwap
            .tenantId(UPDATED_TENANT_ID)
            .clientName(UPDATED_CLIENT_NAME)
            .reason(UPDATED_REASON)
            .resolvedAt(UPDATED_RESOLVED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restSwapMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSwap.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSwap))
            )
            .andExpect(status().isOk());

        // Validate the Swap in the database
        List<Swap> swapList = swapRepository.findAll();
        assertThat(swapList).hasSize(databaseSizeBeforeUpdate);
        Swap testSwap = swapList.get(swapList.size() - 1);
        assertThat(testSwap.getTenantId()).isEqualTo(UPDATED_TENANT_ID);
        assertThat(testSwap.getClientId()).isEqualTo(DEFAULT_CLIENT_ID);
        assertThat(testSwap.getClientName()).isEqualTo(UPDATED_CLIENT_NAME);
        assertThat(testSwap.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testSwap.getReason()).isEqualTo(UPDATED_REASON);
        assertThat(testSwap.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testSwap.getResolvedAt()).isEqualTo(UPDATED_RESOLVED_AT);
        assertThat(testSwap.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void fullUpdateSwapWithPatch() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        int databaseSizeBeforeUpdate = swapRepository.findAll().size();

        // Update the swap using partial update
        Swap partialUpdatedSwap = new Swap();
        partialUpdatedSwap.setId(swap.getId());

        partialUpdatedSwap
            .tenantId(UPDATED_TENANT_ID)
            .clientId(UPDATED_CLIENT_ID)
            .clientName(UPDATED_CLIENT_NAME)
            .status(UPDATED_STATUS)
            .reason(UPDATED_REASON)
            .createdAt(UPDATED_CREATED_AT)
            .resolvedAt(UPDATED_RESOLVED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restSwapMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSwap.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSwap))
            )
            .andExpect(status().isOk());

        // Validate the Swap in the database
        List<Swap> swapList = swapRepository.findAll();
        assertThat(swapList).hasSize(databaseSizeBeforeUpdate);
        Swap testSwap = swapList.get(swapList.size() - 1);
        assertThat(testSwap.getTenantId()).isEqualTo(UPDATED_TENANT_ID);
        assertThat(testSwap.getClientId()).isEqualTo(UPDATED_CLIENT_ID);
        assertThat(testSwap.getClientName()).isEqualTo(UPDATED_CLIENT_NAME);
        assertThat(testSwap.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testSwap.getReason()).isEqualTo(UPDATED_REASON);
        assertThat(testSwap.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testSwap.getResolvedAt()).isEqualTo(UPDATED_RESOLVED_AT);
        assertThat(testSwap.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingSwap() throws Exception {
        int databaseSizeBeforeUpdate = swapRepository.findAll().size();
        swap.setId(count.incrementAndGet());

        // Create the Swap
        SwapDTO swapDTO = swapMapper.toDto(swap);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSwapMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, swapDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(swapDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Swap in the database
        List<Swap> swapList = swapRepository.findAll();
        assertThat(swapList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSwap() throws Exception {
        int databaseSizeBeforeUpdate = swapRepository.findAll().size();
        swap.setId(count.incrementAndGet());

        // Create the Swap
        SwapDTO swapDTO = swapMapper.toDto(swap);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSwapMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(swapDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Swap in the database
        List<Swap> swapList = swapRepository.findAll();
        assertThat(swapList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSwap() throws Exception {
        int databaseSizeBeforeUpdate = swapRepository.findAll().size();
        swap.setId(count.incrementAndGet());

        // Create the Swap
        SwapDTO swapDTO = swapMapper.toDto(swap);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSwapMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(swapDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Swap in the database
        List<Swap> swapList = swapRepository.findAll();
        assertThat(swapList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSwap() throws Exception {
        // Initialize the database
        swapRepository.saveAndFlush(swap);

        int databaseSizeBeforeDelete = swapRepository.findAll().size();

        // Delete the swap
        restSwapMockMvc
            .perform(delete(ENTITY_API_URL_ID, swap.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Swap> swapList = swapRepository.findAll();
        assertThat(swapList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
