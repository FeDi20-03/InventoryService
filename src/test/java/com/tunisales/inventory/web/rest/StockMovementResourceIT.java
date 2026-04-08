package com.tunisales.inventory.web.rest;

import static com.tunisales.inventory.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tunisales.inventory.IntegrationTest;
import com.tunisales.inventory.domain.StockItem;
import com.tunisales.inventory.domain.StockMovement;
import com.tunisales.inventory.domain.Warehouse;
import com.tunisales.inventory.domain.enumeration.MovementType;
import com.tunisales.inventory.repository.StockMovementRepository;
import com.tunisales.inventory.service.StockMovementService;
import com.tunisales.inventory.service.criteria.StockMovementCriteria;
import com.tunisales.inventory.service.dto.StockMovementDTO;
import com.tunisales.inventory.service.mapper.StockMovementMapper;
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
 * Integration tests for the {@link StockMovementResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StockMovementResourceIT {

    private static final MovementType DEFAULT_MOVEMENT_TYPE = MovementType.INBOUND;
    private static final MovementType UPDATED_MOVEMENT_TYPE = MovementType.OUTBOUND;

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;
    private static final Integer SMALLER_QUANTITY = 1 - 1;

    private static final String DEFAULT_PERFORMED_BY_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_PERFORMED_BY_LOGIN = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/stock-movements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Mock
    private StockMovementRepository stockMovementRepositoryMock;

    @Autowired
    private StockMovementMapper stockMovementMapper;

    @Mock
    private StockMovementService stockMovementServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStockMovementMockMvc;

    private StockMovement stockMovement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockMovement createEntity(EntityManager em) {
        StockMovement stockMovement = new StockMovement()
            .movementType(DEFAULT_MOVEMENT_TYPE)
            .reason(DEFAULT_REASON)
            .reference(DEFAULT_REFERENCE)
            .quantity(DEFAULT_QUANTITY)
            .performedByLogin(DEFAULT_PERFORMED_BY_LOGIN)
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
        stockMovement.setStockItem(stockItem);
        return stockMovement;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockMovement createUpdatedEntity(EntityManager em) {
        StockMovement stockMovement = new StockMovement()
            .movementType(UPDATED_MOVEMENT_TYPE)
            .reason(UPDATED_REASON)
            .reference(UPDATED_REFERENCE)
            .quantity(UPDATED_QUANTITY)
            .performedByLogin(UPDATED_PERFORMED_BY_LOGIN)
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
        stockMovement.setStockItem(stockItem);
        return stockMovement;
    }

    @BeforeEach
    public void initTest() {
        stockMovement = createEntity(em);
    }

    @Test
    @Transactional
    void createStockMovement() throws Exception {
        int databaseSizeBeforeCreate = stockMovementRepository.findAll().size();
        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);
        restStockMovementMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockMovementDTO))
            )
            .andExpect(status().isCreated());

        // Validate the StockMovement in the database
        List<StockMovement> stockMovementList = stockMovementRepository.findAll();
        assertThat(stockMovementList).hasSize(databaseSizeBeforeCreate + 1);
        StockMovement testStockMovement = stockMovementList.get(stockMovementList.size() - 1);
        assertThat(testStockMovement.getMovementType()).isEqualTo(DEFAULT_MOVEMENT_TYPE);
        assertThat(testStockMovement.getReason()).isEqualTo(DEFAULT_REASON);
        assertThat(testStockMovement.getReference()).isEqualTo(DEFAULT_REFERENCE);
        assertThat(testStockMovement.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testStockMovement.getPerformedByLogin()).isEqualTo(DEFAULT_PERFORMED_BY_LOGIN);
        assertThat(testStockMovement.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void createStockMovementWithExistingId() throws Exception {
        // Create the StockMovement with an existing ID
        stockMovement.setId(1L);
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        int databaseSizeBeforeCreate = stockMovementRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStockMovementMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockMovementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockMovement in the database
        List<StockMovement> stockMovementList = stockMovementRepository.findAll();
        assertThat(stockMovementList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMovementTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockMovementRepository.findAll().size();
        // set the field null
        stockMovement.setMovementType(null);

        // Create the StockMovement, which fails.
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        restStockMovementMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockMovementDTO))
            )
            .andExpect(status().isBadRequest());

        List<StockMovement> stockMovementList = stockMovementRepository.findAll();
        assertThat(stockMovementList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockMovementRepository.findAll().size();
        // set the field null
        stockMovement.setQuantity(null);

        // Create the StockMovement, which fails.
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        restStockMovementMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockMovementDTO))
            )
            .andExpect(status().isBadRequest());

        List<StockMovement> stockMovementList = stockMovementRepository.findAll();
        assertThat(stockMovementList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockMovementRepository.findAll().size();
        // set the field null
        stockMovement.setCreatedAt(null);

        // Create the StockMovement, which fails.
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        restStockMovementMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockMovementDTO))
            )
            .andExpect(status().isBadRequest());

        List<StockMovement> stockMovementList = stockMovementRepository.findAll();
        assertThat(stockMovementList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStockMovements() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList
        restStockMovementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockMovement.getId().intValue())))
            .andExpect(jsonPath("$.[*].movementType").value(hasItem(DEFAULT_MOVEMENT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].performedByLogin").value(hasItem(DEFAULT_PERFORMED_BY_LOGIN)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStockMovementsWithEagerRelationshipsIsEnabled() throws Exception {
        when(stockMovementServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStockMovementMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(stockMovementServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStockMovementsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(stockMovementServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStockMovementMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(stockMovementRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getStockMovement() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get the stockMovement
        restStockMovementMockMvc
            .perform(get(ENTITY_API_URL_ID, stockMovement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stockMovement.getId().intValue()))
            .andExpect(jsonPath("$.movementType").value(DEFAULT_MOVEMENT_TYPE.toString()))
            .andExpect(jsonPath("$.reason").value(DEFAULT_REASON))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.performedByLogin").value(DEFAULT_PERFORMED_BY_LOGIN))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)));
    }

    @Test
    @Transactional
    void getStockMovementsByIdFiltering() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        Long id = stockMovement.getId();

        defaultStockMovementShouldBeFound("id.equals=" + id);
        defaultStockMovementShouldNotBeFound("id.notEquals=" + id);

        defaultStockMovementShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultStockMovementShouldNotBeFound("id.greaterThan=" + id);

        defaultStockMovementShouldBeFound("id.lessThanOrEqual=" + id);
        defaultStockMovementShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStockMovementsByMovementTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where movementType equals to DEFAULT_MOVEMENT_TYPE
        defaultStockMovementShouldBeFound("movementType.equals=" + DEFAULT_MOVEMENT_TYPE);

        // Get all the stockMovementList where movementType equals to UPDATED_MOVEMENT_TYPE
        defaultStockMovementShouldNotBeFound("movementType.equals=" + UPDATED_MOVEMENT_TYPE);
    }

    @Test
    @Transactional
    void getAllStockMovementsByMovementTypeIsInShouldWork() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where movementType in DEFAULT_MOVEMENT_TYPE or UPDATED_MOVEMENT_TYPE
        defaultStockMovementShouldBeFound("movementType.in=" + DEFAULT_MOVEMENT_TYPE + "," + UPDATED_MOVEMENT_TYPE);

        // Get all the stockMovementList where movementType equals to UPDATED_MOVEMENT_TYPE
        defaultStockMovementShouldNotBeFound("movementType.in=" + UPDATED_MOVEMENT_TYPE);
    }

    @Test
    @Transactional
    void getAllStockMovementsByMovementTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where movementType is not null
        defaultStockMovementShouldBeFound("movementType.specified=true");

        // Get all the stockMovementList where movementType is null
        defaultStockMovementShouldNotBeFound("movementType.specified=false");
    }

    @Test
    @Transactional
    void getAllStockMovementsByReasonIsEqualToSomething() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where reason equals to DEFAULT_REASON
        defaultStockMovementShouldBeFound("reason.equals=" + DEFAULT_REASON);

        // Get all the stockMovementList where reason equals to UPDATED_REASON
        defaultStockMovementShouldNotBeFound("reason.equals=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllStockMovementsByReasonIsInShouldWork() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where reason in DEFAULT_REASON or UPDATED_REASON
        defaultStockMovementShouldBeFound("reason.in=" + DEFAULT_REASON + "," + UPDATED_REASON);

        // Get all the stockMovementList where reason equals to UPDATED_REASON
        defaultStockMovementShouldNotBeFound("reason.in=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllStockMovementsByReasonIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where reason is not null
        defaultStockMovementShouldBeFound("reason.specified=true");

        // Get all the stockMovementList where reason is null
        defaultStockMovementShouldNotBeFound("reason.specified=false");
    }

    @Test
    @Transactional
    void getAllStockMovementsByReasonContainsSomething() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where reason contains DEFAULT_REASON
        defaultStockMovementShouldBeFound("reason.contains=" + DEFAULT_REASON);

        // Get all the stockMovementList where reason contains UPDATED_REASON
        defaultStockMovementShouldNotBeFound("reason.contains=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllStockMovementsByReasonNotContainsSomething() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where reason does not contain DEFAULT_REASON
        defaultStockMovementShouldNotBeFound("reason.doesNotContain=" + DEFAULT_REASON);

        // Get all the stockMovementList where reason does not contain UPDATED_REASON
        defaultStockMovementShouldBeFound("reason.doesNotContain=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllStockMovementsByReferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where reference equals to DEFAULT_REFERENCE
        defaultStockMovementShouldBeFound("reference.equals=" + DEFAULT_REFERENCE);

        // Get all the stockMovementList where reference equals to UPDATED_REFERENCE
        defaultStockMovementShouldNotBeFound("reference.equals=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllStockMovementsByReferenceIsInShouldWork() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where reference in DEFAULT_REFERENCE or UPDATED_REFERENCE
        defaultStockMovementShouldBeFound("reference.in=" + DEFAULT_REFERENCE + "," + UPDATED_REFERENCE);

        // Get all the stockMovementList where reference equals to UPDATED_REFERENCE
        defaultStockMovementShouldNotBeFound("reference.in=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllStockMovementsByReferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where reference is not null
        defaultStockMovementShouldBeFound("reference.specified=true");

        // Get all the stockMovementList where reference is null
        defaultStockMovementShouldNotBeFound("reference.specified=false");
    }

    @Test
    @Transactional
    void getAllStockMovementsByReferenceContainsSomething() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where reference contains DEFAULT_REFERENCE
        defaultStockMovementShouldBeFound("reference.contains=" + DEFAULT_REFERENCE);

        // Get all the stockMovementList where reference contains UPDATED_REFERENCE
        defaultStockMovementShouldNotBeFound("reference.contains=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllStockMovementsByReferenceNotContainsSomething() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where reference does not contain DEFAULT_REFERENCE
        defaultStockMovementShouldNotBeFound("reference.doesNotContain=" + DEFAULT_REFERENCE);

        // Get all the stockMovementList where reference does not contain UPDATED_REFERENCE
        defaultStockMovementShouldBeFound("reference.doesNotContain=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllStockMovementsByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where quantity equals to DEFAULT_QUANTITY
        defaultStockMovementShouldBeFound("quantity.equals=" + DEFAULT_QUANTITY);

        // Get all the stockMovementList where quantity equals to UPDATED_QUANTITY
        defaultStockMovementShouldNotBeFound("quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllStockMovementsByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where quantity in DEFAULT_QUANTITY or UPDATED_QUANTITY
        defaultStockMovementShouldBeFound("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY);

        // Get all the stockMovementList where quantity equals to UPDATED_QUANTITY
        defaultStockMovementShouldNotBeFound("quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllStockMovementsByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where quantity is not null
        defaultStockMovementShouldBeFound("quantity.specified=true");

        // Get all the stockMovementList where quantity is null
        defaultStockMovementShouldNotBeFound("quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllStockMovementsByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where quantity is greater than or equal to DEFAULT_QUANTITY
        defaultStockMovementShouldBeFound("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the stockMovementList where quantity is greater than or equal to UPDATED_QUANTITY
        defaultStockMovementShouldNotBeFound("quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllStockMovementsByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where quantity is less than or equal to DEFAULT_QUANTITY
        defaultStockMovementShouldBeFound("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the stockMovementList where quantity is less than or equal to SMALLER_QUANTITY
        defaultStockMovementShouldNotBeFound("quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllStockMovementsByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where quantity is less than DEFAULT_QUANTITY
        defaultStockMovementShouldNotBeFound("quantity.lessThan=" + DEFAULT_QUANTITY);

        // Get all the stockMovementList where quantity is less than UPDATED_QUANTITY
        defaultStockMovementShouldBeFound("quantity.lessThan=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllStockMovementsByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where quantity is greater than DEFAULT_QUANTITY
        defaultStockMovementShouldNotBeFound("quantity.greaterThan=" + DEFAULT_QUANTITY);

        // Get all the stockMovementList where quantity is greater than SMALLER_QUANTITY
        defaultStockMovementShouldBeFound("quantity.greaterThan=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllStockMovementsByPerformedByLoginIsEqualToSomething() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where performedByLogin equals to DEFAULT_PERFORMED_BY_LOGIN
        defaultStockMovementShouldBeFound("performedByLogin.equals=" + DEFAULT_PERFORMED_BY_LOGIN);

        // Get all the stockMovementList where performedByLogin equals to UPDATED_PERFORMED_BY_LOGIN
        defaultStockMovementShouldNotBeFound("performedByLogin.equals=" + UPDATED_PERFORMED_BY_LOGIN);
    }

    @Test
    @Transactional
    void getAllStockMovementsByPerformedByLoginIsInShouldWork() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where performedByLogin in DEFAULT_PERFORMED_BY_LOGIN or UPDATED_PERFORMED_BY_LOGIN
        defaultStockMovementShouldBeFound("performedByLogin.in=" + DEFAULT_PERFORMED_BY_LOGIN + "," + UPDATED_PERFORMED_BY_LOGIN);

        // Get all the stockMovementList where performedByLogin equals to UPDATED_PERFORMED_BY_LOGIN
        defaultStockMovementShouldNotBeFound("performedByLogin.in=" + UPDATED_PERFORMED_BY_LOGIN);
    }

    @Test
    @Transactional
    void getAllStockMovementsByPerformedByLoginIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where performedByLogin is not null
        defaultStockMovementShouldBeFound("performedByLogin.specified=true");

        // Get all the stockMovementList where performedByLogin is null
        defaultStockMovementShouldNotBeFound("performedByLogin.specified=false");
    }

    @Test
    @Transactional
    void getAllStockMovementsByPerformedByLoginContainsSomething() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where performedByLogin contains DEFAULT_PERFORMED_BY_LOGIN
        defaultStockMovementShouldBeFound("performedByLogin.contains=" + DEFAULT_PERFORMED_BY_LOGIN);

        // Get all the stockMovementList where performedByLogin contains UPDATED_PERFORMED_BY_LOGIN
        defaultStockMovementShouldNotBeFound("performedByLogin.contains=" + UPDATED_PERFORMED_BY_LOGIN);
    }

    @Test
    @Transactional
    void getAllStockMovementsByPerformedByLoginNotContainsSomething() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where performedByLogin does not contain DEFAULT_PERFORMED_BY_LOGIN
        defaultStockMovementShouldNotBeFound("performedByLogin.doesNotContain=" + DEFAULT_PERFORMED_BY_LOGIN);

        // Get all the stockMovementList where performedByLogin does not contain UPDATED_PERFORMED_BY_LOGIN
        defaultStockMovementShouldBeFound("performedByLogin.doesNotContain=" + UPDATED_PERFORMED_BY_LOGIN);
    }

    @Test
    @Transactional
    void getAllStockMovementsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where createdAt equals to DEFAULT_CREATED_AT
        defaultStockMovementShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the stockMovementList where createdAt equals to UPDATED_CREATED_AT
        defaultStockMovementShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllStockMovementsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultStockMovementShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the stockMovementList where createdAt equals to UPDATED_CREATED_AT
        defaultStockMovementShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllStockMovementsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where createdAt is not null
        defaultStockMovementShouldBeFound("createdAt.specified=true");

        // Get all the stockMovementList where createdAt is null
        defaultStockMovementShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllStockMovementsByCreatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where createdAt is greater than or equal to DEFAULT_CREATED_AT
        defaultStockMovementShouldBeFound("createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the stockMovementList where createdAt is greater than or equal to UPDATED_CREATED_AT
        defaultStockMovementShouldNotBeFound("createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllStockMovementsByCreatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where createdAt is less than or equal to DEFAULT_CREATED_AT
        defaultStockMovementShouldBeFound("createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the stockMovementList where createdAt is less than or equal to SMALLER_CREATED_AT
        defaultStockMovementShouldNotBeFound("createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllStockMovementsByCreatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where createdAt is less than DEFAULT_CREATED_AT
        defaultStockMovementShouldNotBeFound("createdAt.lessThan=" + DEFAULT_CREATED_AT);

        // Get all the stockMovementList where createdAt is less than UPDATED_CREATED_AT
        defaultStockMovementShouldBeFound("createdAt.lessThan=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllStockMovementsByCreatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        // Get all the stockMovementList where createdAt is greater than DEFAULT_CREATED_AT
        defaultStockMovementShouldNotBeFound("createdAt.greaterThan=" + DEFAULT_CREATED_AT);

        // Get all the stockMovementList where createdAt is greater than SMALLER_CREATED_AT
        defaultStockMovementShouldBeFound("createdAt.greaterThan=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllStockMovementsByFromWarehouseIsEqualToSomething() throws Exception {
        Warehouse fromWarehouse;
        if (TestUtil.findAll(em, Warehouse.class).isEmpty()) {
            stockMovementRepository.saveAndFlush(stockMovement);
            fromWarehouse = WarehouseResourceIT.createEntity(em);
        } else {
            fromWarehouse = TestUtil.findAll(em, Warehouse.class).get(0);
        }
        em.persist(fromWarehouse);
        em.flush();
        stockMovement.setFromWarehouse(fromWarehouse);
        stockMovementRepository.saveAndFlush(stockMovement);
        Long fromWarehouseId = fromWarehouse.getId();

        // Get all the stockMovementList where fromWarehouse equals to fromWarehouseId
        defaultStockMovementShouldBeFound("fromWarehouseId.equals=" + fromWarehouseId);

        // Get all the stockMovementList where fromWarehouse equals to (fromWarehouseId + 1)
        defaultStockMovementShouldNotBeFound("fromWarehouseId.equals=" + (fromWarehouseId + 1));
    }

    @Test
    @Transactional
    void getAllStockMovementsByToWarehouseIsEqualToSomething() throws Exception {
        Warehouse toWarehouse;
        if (TestUtil.findAll(em, Warehouse.class).isEmpty()) {
            stockMovementRepository.saveAndFlush(stockMovement);
            toWarehouse = WarehouseResourceIT.createEntity(em);
        } else {
            toWarehouse = TestUtil.findAll(em, Warehouse.class).get(0);
        }
        em.persist(toWarehouse);
        em.flush();
        stockMovement.setToWarehouse(toWarehouse);
        stockMovementRepository.saveAndFlush(stockMovement);
        Long toWarehouseId = toWarehouse.getId();

        // Get all the stockMovementList where toWarehouse equals to toWarehouseId
        defaultStockMovementShouldBeFound("toWarehouseId.equals=" + toWarehouseId);

        // Get all the stockMovementList where toWarehouse equals to (toWarehouseId + 1)
        defaultStockMovementShouldNotBeFound("toWarehouseId.equals=" + (toWarehouseId + 1));
    }

    @Test
    @Transactional
    void getAllStockMovementsByStockItemIsEqualToSomething() throws Exception {
        StockItem stockItem;
        if (TestUtil.findAll(em, StockItem.class).isEmpty()) {
            stockMovementRepository.saveAndFlush(stockMovement);
            stockItem = StockItemResourceIT.createEntity(em);
        } else {
            stockItem = TestUtil.findAll(em, StockItem.class).get(0);
        }
        em.persist(stockItem);
        em.flush();
        stockMovement.setStockItem(stockItem);
        stockMovementRepository.saveAndFlush(stockMovement);
        Long stockItemId = stockItem.getId();

        // Get all the stockMovementList where stockItem equals to stockItemId
        defaultStockMovementShouldBeFound("stockItemId.equals=" + stockItemId);

        // Get all the stockMovementList where stockItem equals to (stockItemId + 1)
        defaultStockMovementShouldNotBeFound("stockItemId.equals=" + (stockItemId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStockMovementShouldBeFound(String filter) throws Exception {
        restStockMovementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockMovement.getId().intValue())))
            .andExpect(jsonPath("$.[*].movementType").value(hasItem(DEFAULT_MOVEMENT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].performedByLogin").value(hasItem(DEFAULT_PERFORMED_BY_LOGIN)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));

        // Check, that the count call also returns 1
        restStockMovementMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStockMovementShouldNotBeFound(String filter) throws Exception {
        restStockMovementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStockMovementMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStockMovement() throws Exception {
        // Get the stockMovement
        restStockMovementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStockMovement() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        int databaseSizeBeforeUpdate = stockMovementRepository.findAll().size();

        // Update the stockMovement
        StockMovement updatedStockMovement = stockMovementRepository.findById(stockMovement.getId()).get();
        // Disconnect from session so that the updates on updatedStockMovement are not directly saved in db
        em.detach(updatedStockMovement);
        updatedStockMovement
            .movementType(UPDATED_MOVEMENT_TYPE)
            .reason(UPDATED_REASON)
            .reference(UPDATED_REFERENCE)
            .quantity(UPDATED_QUANTITY)
            .performedByLogin(UPDATED_PERFORMED_BY_LOGIN)
            .createdAt(UPDATED_CREATED_AT);
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(updatedStockMovement);

        restStockMovementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockMovementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockMovementDTO))
            )
            .andExpect(status().isOk());

        // Validate the StockMovement in the database
        List<StockMovement> stockMovementList = stockMovementRepository.findAll();
        assertThat(stockMovementList).hasSize(databaseSizeBeforeUpdate);
        StockMovement testStockMovement = stockMovementList.get(stockMovementList.size() - 1);
        assertThat(testStockMovement.getMovementType()).isEqualTo(UPDATED_MOVEMENT_TYPE);
        assertThat(testStockMovement.getReason()).isEqualTo(UPDATED_REASON);
        assertThat(testStockMovement.getReference()).isEqualTo(UPDATED_REFERENCE);
        assertThat(testStockMovement.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testStockMovement.getPerformedByLogin()).isEqualTo(UPDATED_PERFORMED_BY_LOGIN);
        assertThat(testStockMovement.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingStockMovement() throws Exception {
        int databaseSizeBeforeUpdate = stockMovementRepository.findAll().size();
        stockMovement.setId(count.incrementAndGet());

        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockMovementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockMovementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockMovementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockMovement in the database
        List<StockMovement> stockMovementList = stockMovementRepository.findAll();
        assertThat(stockMovementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStockMovement() throws Exception {
        int databaseSizeBeforeUpdate = stockMovementRepository.findAll().size();
        stockMovement.setId(count.incrementAndGet());

        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMovementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockMovementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockMovement in the database
        List<StockMovement> stockMovementList = stockMovementRepository.findAll();
        assertThat(stockMovementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStockMovement() throws Exception {
        int databaseSizeBeforeUpdate = stockMovementRepository.findAll().size();
        stockMovement.setId(count.incrementAndGet());

        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMovementMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockMovementDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockMovement in the database
        List<StockMovement> stockMovementList = stockMovementRepository.findAll();
        assertThat(stockMovementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStockMovementWithPatch() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        int databaseSizeBeforeUpdate = stockMovementRepository.findAll().size();

        // Update the stockMovement using partial update
        StockMovement partialUpdatedStockMovement = new StockMovement();
        partialUpdatedStockMovement.setId(stockMovement.getId());

        partialUpdatedStockMovement.reason(UPDATED_REASON).quantity(UPDATED_QUANTITY);

        restStockMovementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockMovement.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStockMovement))
            )
            .andExpect(status().isOk());

        // Validate the StockMovement in the database
        List<StockMovement> stockMovementList = stockMovementRepository.findAll();
        assertThat(stockMovementList).hasSize(databaseSizeBeforeUpdate);
        StockMovement testStockMovement = stockMovementList.get(stockMovementList.size() - 1);
        assertThat(testStockMovement.getMovementType()).isEqualTo(DEFAULT_MOVEMENT_TYPE);
        assertThat(testStockMovement.getReason()).isEqualTo(UPDATED_REASON);
        assertThat(testStockMovement.getReference()).isEqualTo(DEFAULT_REFERENCE);
        assertThat(testStockMovement.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testStockMovement.getPerformedByLogin()).isEqualTo(DEFAULT_PERFORMED_BY_LOGIN);
        assertThat(testStockMovement.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void fullUpdateStockMovementWithPatch() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        int databaseSizeBeforeUpdate = stockMovementRepository.findAll().size();

        // Update the stockMovement using partial update
        StockMovement partialUpdatedStockMovement = new StockMovement();
        partialUpdatedStockMovement.setId(stockMovement.getId());

        partialUpdatedStockMovement
            .movementType(UPDATED_MOVEMENT_TYPE)
            .reason(UPDATED_REASON)
            .reference(UPDATED_REFERENCE)
            .quantity(UPDATED_QUANTITY)
            .performedByLogin(UPDATED_PERFORMED_BY_LOGIN)
            .createdAt(UPDATED_CREATED_AT);

        restStockMovementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockMovement.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStockMovement))
            )
            .andExpect(status().isOk());

        // Validate the StockMovement in the database
        List<StockMovement> stockMovementList = stockMovementRepository.findAll();
        assertThat(stockMovementList).hasSize(databaseSizeBeforeUpdate);
        StockMovement testStockMovement = stockMovementList.get(stockMovementList.size() - 1);
        assertThat(testStockMovement.getMovementType()).isEqualTo(UPDATED_MOVEMENT_TYPE);
        assertThat(testStockMovement.getReason()).isEqualTo(UPDATED_REASON);
        assertThat(testStockMovement.getReference()).isEqualTo(UPDATED_REFERENCE);
        assertThat(testStockMovement.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testStockMovement.getPerformedByLogin()).isEqualTo(UPDATED_PERFORMED_BY_LOGIN);
        assertThat(testStockMovement.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingStockMovement() throws Exception {
        int databaseSizeBeforeUpdate = stockMovementRepository.findAll().size();
        stockMovement.setId(count.incrementAndGet());

        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockMovementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stockMovementDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockMovementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockMovement in the database
        List<StockMovement> stockMovementList = stockMovementRepository.findAll();
        assertThat(stockMovementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStockMovement() throws Exception {
        int databaseSizeBeforeUpdate = stockMovementRepository.findAll().size();
        stockMovement.setId(count.incrementAndGet());

        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMovementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockMovementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockMovement in the database
        List<StockMovement> stockMovementList = stockMovementRepository.findAll();
        assertThat(stockMovementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStockMovement() throws Exception {
        int databaseSizeBeforeUpdate = stockMovementRepository.findAll().size();
        stockMovement.setId(count.incrementAndGet());

        // Create the StockMovement
        StockMovementDTO stockMovementDTO = stockMovementMapper.toDto(stockMovement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockMovementMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockMovementDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockMovement in the database
        List<StockMovement> stockMovementList = stockMovementRepository.findAll();
        assertThat(stockMovementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStockMovement() throws Exception {
        // Initialize the database
        stockMovementRepository.saveAndFlush(stockMovement);

        int databaseSizeBeforeDelete = stockMovementRepository.findAll().size();

        // Delete the stockMovement
        restStockMovementMockMvc
            .perform(delete(ENTITY_API_URL_ID, stockMovement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<StockMovement> stockMovementList = stockMovementRepository.findAll();
        assertThat(stockMovementList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
