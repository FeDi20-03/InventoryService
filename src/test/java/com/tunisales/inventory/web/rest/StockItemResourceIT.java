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
import com.tunisales.inventory.domain.enumeration.StockItemStatus;
import com.tunisales.inventory.repository.StockItemRepository;
import com.tunisales.inventory.service.StockItemService;
import com.tunisales.inventory.service.criteria.StockItemCriteria;
import com.tunisales.inventory.service.dto.StockItemDTO;
import com.tunisales.inventory.service.mapper.StockItemMapper;
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
 * Integration tests for the {@link StockItemResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StockItemResourceIT {

    private static final Long DEFAULT_TENANT_ID = 1L;
    private static final Long UPDATED_TENANT_ID = 2L;
    private static final Long SMALLER_TENANT_ID = 1L - 1L;

    private static final Long DEFAULT_PRODUCT_ID = 1L;
    private static final Long UPDATED_PRODUCT_ID = 2L;
    private static final Long SMALLER_PRODUCT_ID = 1L - 1L;

    private static final String DEFAULT_PRODUCT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_PRODUCT_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_IMEI = "AAAAAAAAAAAAAAA";
    private static final String UPDATED_IMEI = "BBBBBBBBBBBBBBB";

    private static final StockItemStatus DEFAULT_STATUS = StockItemStatus.AVAILABLE;
    private static final StockItemStatus UPDATED_STATUS = StockItemStatus.RESERVED;

    private static final Boolean DEFAULT_IS_DELETED = false;
    private static final Boolean UPDATED_IS_DELETED = true;

    private static final ZonedDateTime DEFAULT_ACQUIRED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ACQUIRED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_ACQUIRED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/stock-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StockItemRepository stockItemRepository;

    @Mock
    private StockItemRepository stockItemRepositoryMock;

    @Autowired
    private StockItemMapper stockItemMapper;

    @Mock
    private StockItemService stockItemServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStockItemMockMvc;

    private StockItem stockItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockItem createEntity(EntityManager em) {
        StockItem stockItem = new StockItem()
            .tenantId(DEFAULT_TENANT_ID)
            .productId(DEFAULT_PRODUCT_ID)
            .productName(DEFAULT_PRODUCT_NAME)
            .imei(DEFAULT_IMEI)
            .status(DEFAULT_STATUS)
            .isDeleted(DEFAULT_IS_DELETED)
            .acquiredAt(DEFAULT_ACQUIRED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        // Add required entity
        Warehouse warehouse;
        if (TestUtil.findAll(em, Warehouse.class).isEmpty()) {
            warehouse = WarehouseResourceIT.createEntity(em);
            em.persist(warehouse);
            em.flush();
        } else {
            warehouse = TestUtil.findAll(em, Warehouse.class).get(0);
        }
        stockItem.setWarehouse(warehouse);
        return stockItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockItem createUpdatedEntity(EntityManager em) {
        StockItem stockItem = new StockItem()
            .tenantId(UPDATED_TENANT_ID)
            .productId(UPDATED_PRODUCT_ID)
            .productName(UPDATED_PRODUCT_NAME)
            .imei(UPDATED_IMEI)
            .status(UPDATED_STATUS)
            .isDeleted(UPDATED_IS_DELETED)
            .acquiredAt(UPDATED_ACQUIRED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        // Add required entity
        Warehouse warehouse;
        if (TestUtil.findAll(em, Warehouse.class).isEmpty()) {
            warehouse = WarehouseResourceIT.createUpdatedEntity(em);
            em.persist(warehouse);
            em.flush();
        } else {
            warehouse = TestUtil.findAll(em, Warehouse.class).get(0);
        }
        stockItem.setWarehouse(warehouse);
        return stockItem;
    }

    @BeforeEach
    public void initTest() {
        stockItem = createEntity(em);
    }

    @Test
    @Transactional
    void createStockItem() throws Exception {
        int databaseSizeBeforeCreate = stockItemRepository.findAll().size();
        // Create the StockItem
        StockItemDTO stockItemDTO = stockItemMapper.toDto(stockItem);
        restStockItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockItemDTO)))
            .andExpect(status().isCreated());

        // Validate the StockItem in the database
        List<StockItem> stockItemList = stockItemRepository.findAll();
        assertThat(stockItemList).hasSize(databaseSizeBeforeCreate + 1);
        StockItem testStockItem = stockItemList.get(stockItemList.size() - 1);
        assertThat(testStockItem.getTenantId()).isEqualTo(DEFAULT_TENANT_ID);
        assertThat(testStockItem.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
        assertThat(testStockItem.getProductName()).isEqualTo(DEFAULT_PRODUCT_NAME);
        assertThat(testStockItem.getImei()).isEqualTo(DEFAULT_IMEI);
        assertThat(testStockItem.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testStockItem.getIsDeleted()).isEqualTo(DEFAULT_IS_DELETED);
        assertThat(testStockItem.getAcquiredAt()).isEqualTo(DEFAULT_ACQUIRED_AT);
        assertThat(testStockItem.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    @Transactional
    void createStockItemWithExistingId() throws Exception {
        // Create the StockItem with an existing ID
        stockItem.setId(1L);
        StockItemDTO stockItemDTO = stockItemMapper.toDto(stockItem);

        int databaseSizeBeforeCreate = stockItemRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStockItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the StockItem in the database
        List<StockItem> stockItemList = stockItemRepository.findAll();
        assertThat(stockItemList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTenantIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockItemRepository.findAll().size();
        // set the field null
        stockItem.setTenantId(null);

        // Create the StockItem, which fails.
        StockItemDTO stockItemDTO = stockItemMapper.toDto(stockItem);

        restStockItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockItemDTO)))
            .andExpect(status().isBadRequest());

        List<StockItem> stockItemList = stockItemRepository.findAll();
        assertThat(stockItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkProductIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockItemRepository.findAll().size();
        // set the field null
        stockItem.setProductId(null);

        // Create the StockItem, which fails.
        StockItemDTO stockItemDTO = stockItemMapper.toDto(stockItem);

        restStockItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockItemDTO)))
            .andExpect(status().isBadRequest());

        List<StockItem> stockItemList = stockItemRepository.findAll();
        assertThat(stockItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkImeiIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockItemRepository.findAll().size();
        // set the field null
        stockItem.setImei(null);

        // Create the StockItem, which fails.
        StockItemDTO stockItemDTO = stockItemMapper.toDto(stockItem);

        restStockItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockItemDTO)))
            .andExpect(status().isBadRequest());

        List<StockItem> stockItemList = stockItemRepository.findAll();
        assertThat(stockItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockItemRepository.findAll().size();
        // set the field null
        stockItem.setStatus(null);

        // Create the StockItem, which fails.
        StockItemDTO stockItemDTO = stockItemMapper.toDto(stockItem);

        restStockItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockItemDTO)))
            .andExpect(status().isBadRequest());

        List<StockItem> stockItemList = stockItemRepository.findAll();
        assertThat(stockItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsDeletedIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockItemRepository.findAll().size();
        // set the field null
        stockItem.setIsDeleted(null);

        // Create the StockItem, which fails.
        StockItemDTO stockItemDTO = stockItemMapper.toDto(stockItem);

        restStockItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockItemDTO)))
            .andExpect(status().isBadRequest());

        List<StockItem> stockItemList = stockItemRepository.findAll();
        assertThat(stockItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAcquiredAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockItemRepository.findAll().size();
        // set the field null
        stockItem.setAcquiredAt(null);

        // Create the StockItem, which fails.
        StockItemDTO stockItemDTO = stockItemMapper.toDto(stockItem);

        restStockItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockItemDTO)))
            .andExpect(status().isBadRequest());

        List<StockItem> stockItemList = stockItemRepository.findAll();
        assertThat(stockItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStockItems() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList
        restStockItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].tenantId").value(hasItem(DEFAULT_TENANT_ID.intValue())))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.intValue())))
            .andExpect(jsonPath("$.[*].productName").value(hasItem(DEFAULT_PRODUCT_NAME)))
            .andExpect(jsonPath("$.[*].imei").value(hasItem(DEFAULT_IMEI)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED.booleanValue())))
            .andExpect(jsonPath("$.[*].acquiredAt").value(hasItem(sameInstant(DEFAULT_ACQUIRED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStockItemsWithEagerRelationshipsIsEnabled() throws Exception {
        when(stockItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStockItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(stockItemServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStockItemsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(stockItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStockItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(stockItemRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getStockItem() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get the stockItem
        restStockItemMockMvc
            .perform(get(ENTITY_API_URL_ID, stockItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stockItem.getId().intValue()))
            .andExpect(jsonPath("$.tenantId").value(DEFAULT_TENANT_ID.intValue()))
            .andExpect(jsonPath("$.productId").value(DEFAULT_PRODUCT_ID.intValue()))
            .andExpect(jsonPath("$.productName").value(DEFAULT_PRODUCT_NAME))
            .andExpect(jsonPath("$.imei").value(DEFAULT_IMEI))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED.booleanValue()))
            .andExpect(jsonPath("$.acquiredAt").value(sameInstant(DEFAULT_ACQUIRED_AT)))
            .andExpect(jsonPath("$.updatedAt").value(sameInstant(DEFAULT_UPDATED_AT)));
    }

    @Test
    @Transactional
    void getStockItemsByIdFiltering() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        Long id = stockItem.getId();

        defaultStockItemShouldBeFound("id.equals=" + id);
        defaultStockItemShouldNotBeFound("id.notEquals=" + id);

        defaultStockItemShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultStockItemShouldNotBeFound("id.greaterThan=" + id);

        defaultStockItemShouldBeFound("id.lessThanOrEqual=" + id);
        defaultStockItemShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStockItemsByTenantIdIsEqualToSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where tenantId equals to DEFAULT_TENANT_ID
        defaultStockItemShouldBeFound("tenantId.equals=" + DEFAULT_TENANT_ID);

        // Get all the stockItemList where tenantId equals to UPDATED_TENANT_ID
        defaultStockItemShouldNotBeFound("tenantId.equals=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllStockItemsByTenantIdIsInShouldWork() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where tenantId in DEFAULT_TENANT_ID or UPDATED_TENANT_ID
        defaultStockItemShouldBeFound("tenantId.in=" + DEFAULT_TENANT_ID + "," + UPDATED_TENANT_ID);

        // Get all the stockItemList where tenantId equals to UPDATED_TENANT_ID
        defaultStockItemShouldNotBeFound("tenantId.in=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllStockItemsByTenantIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where tenantId is not null
        defaultStockItemShouldBeFound("tenantId.specified=true");

        // Get all the stockItemList where tenantId is null
        defaultStockItemShouldNotBeFound("tenantId.specified=false");
    }

    @Test
    @Transactional
    void getAllStockItemsByTenantIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where tenantId is greater than or equal to DEFAULT_TENANT_ID
        defaultStockItemShouldBeFound("tenantId.greaterThanOrEqual=" + DEFAULT_TENANT_ID);

        // Get all the stockItemList where tenantId is greater than or equal to UPDATED_TENANT_ID
        defaultStockItemShouldNotBeFound("tenantId.greaterThanOrEqual=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllStockItemsByTenantIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where tenantId is less than or equal to DEFAULT_TENANT_ID
        defaultStockItemShouldBeFound("tenantId.lessThanOrEqual=" + DEFAULT_TENANT_ID);

        // Get all the stockItemList where tenantId is less than or equal to SMALLER_TENANT_ID
        defaultStockItemShouldNotBeFound("tenantId.lessThanOrEqual=" + SMALLER_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllStockItemsByTenantIdIsLessThanSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where tenantId is less than DEFAULT_TENANT_ID
        defaultStockItemShouldNotBeFound("tenantId.lessThan=" + DEFAULT_TENANT_ID);

        // Get all the stockItemList where tenantId is less than UPDATED_TENANT_ID
        defaultStockItemShouldBeFound("tenantId.lessThan=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllStockItemsByTenantIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where tenantId is greater than DEFAULT_TENANT_ID
        defaultStockItemShouldNotBeFound("tenantId.greaterThan=" + DEFAULT_TENANT_ID);

        // Get all the stockItemList where tenantId is greater than SMALLER_TENANT_ID
        defaultStockItemShouldBeFound("tenantId.greaterThan=" + SMALLER_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllStockItemsByProductIdIsEqualToSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where productId equals to DEFAULT_PRODUCT_ID
        defaultStockItemShouldBeFound("productId.equals=" + DEFAULT_PRODUCT_ID);

        // Get all the stockItemList where productId equals to UPDATED_PRODUCT_ID
        defaultStockItemShouldNotBeFound("productId.equals=" + UPDATED_PRODUCT_ID);
    }

    @Test
    @Transactional
    void getAllStockItemsByProductIdIsInShouldWork() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where productId in DEFAULT_PRODUCT_ID or UPDATED_PRODUCT_ID
        defaultStockItemShouldBeFound("productId.in=" + DEFAULT_PRODUCT_ID + "," + UPDATED_PRODUCT_ID);

        // Get all the stockItemList where productId equals to UPDATED_PRODUCT_ID
        defaultStockItemShouldNotBeFound("productId.in=" + UPDATED_PRODUCT_ID);
    }

    @Test
    @Transactional
    void getAllStockItemsByProductIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where productId is not null
        defaultStockItemShouldBeFound("productId.specified=true");

        // Get all the stockItemList where productId is null
        defaultStockItemShouldNotBeFound("productId.specified=false");
    }

    @Test
    @Transactional
    void getAllStockItemsByProductIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where productId is greater than or equal to DEFAULT_PRODUCT_ID
        defaultStockItemShouldBeFound("productId.greaterThanOrEqual=" + DEFAULT_PRODUCT_ID);

        // Get all the stockItemList where productId is greater than or equal to UPDATED_PRODUCT_ID
        defaultStockItemShouldNotBeFound("productId.greaterThanOrEqual=" + UPDATED_PRODUCT_ID);
    }

    @Test
    @Transactional
    void getAllStockItemsByProductIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where productId is less than or equal to DEFAULT_PRODUCT_ID
        defaultStockItemShouldBeFound("productId.lessThanOrEqual=" + DEFAULT_PRODUCT_ID);

        // Get all the stockItemList where productId is less than or equal to SMALLER_PRODUCT_ID
        defaultStockItemShouldNotBeFound("productId.lessThanOrEqual=" + SMALLER_PRODUCT_ID);
    }

    @Test
    @Transactional
    void getAllStockItemsByProductIdIsLessThanSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where productId is less than DEFAULT_PRODUCT_ID
        defaultStockItemShouldNotBeFound("productId.lessThan=" + DEFAULT_PRODUCT_ID);

        // Get all the stockItemList where productId is less than UPDATED_PRODUCT_ID
        defaultStockItemShouldBeFound("productId.lessThan=" + UPDATED_PRODUCT_ID);
    }

    @Test
    @Transactional
    void getAllStockItemsByProductIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where productId is greater than DEFAULT_PRODUCT_ID
        defaultStockItemShouldNotBeFound("productId.greaterThan=" + DEFAULT_PRODUCT_ID);

        // Get all the stockItemList where productId is greater than SMALLER_PRODUCT_ID
        defaultStockItemShouldBeFound("productId.greaterThan=" + SMALLER_PRODUCT_ID);
    }

    @Test
    @Transactional
    void getAllStockItemsByProductNameIsEqualToSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where productName equals to DEFAULT_PRODUCT_NAME
        defaultStockItemShouldBeFound("productName.equals=" + DEFAULT_PRODUCT_NAME);

        // Get all the stockItemList where productName equals to UPDATED_PRODUCT_NAME
        defaultStockItemShouldNotBeFound("productName.equals=" + UPDATED_PRODUCT_NAME);
    }

    @Test
    @Transactional
    void getAllStockItemsByProductNameIsInShouldWork() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where productName in DEFAULT_PRODUCT_NAME or UPDATED_PRODUCT_NAME
        defaultStockItemShouldBeFound("productName.in=" + DEFAULT_PRODUCT_NAME + "," + UPDATED_PRODUCT_NAME);

        // Get all the stockItemList where productName equals to UPDATED_PRODUCT_NAME
        defaultStockItemShouldNotBeFound("productName.in=" + UPDATED_PRODUCT_NAME);
    }

    @Test
    @Transactional
    void getAllStockItemsByProductNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where productName is not null
        defaultStockItemShouldBeFound("productName.specified=true");

        // Get all the stockItemList where productName is null
        defaultStockItemShouldNotBeFound("productName.specified=false");
    }

    @Test
    @Transactional
    void getAllStockItemsByProductNameContainsSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where productName contains DEFAULT_PRODUCT_NAME
        defaultStockItemShouldBeFound("productName.contains=" + DEFAULT_PRODUCT_NAME);

        // Get all the stockItemList where productName contains UPDATED_PRODUCT_NAME
        defaultStockItemShouldNotBeFound("productName.contains=" + UPDATED_PRODUCT_NAME);
    }

    @Test
    @Transactional
    void getAllStockItemsByProductNameNotContainsSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where productName does not contain DEFAULT_PRODUCT_NAME
        defaultStockItemShouldNotBeFound("productName.doesNotContain=" + DEFAULT_PRODUCT_NAME);

        // Get all the stockItemList where productName does not contain UPDATED_PRODUCT_NAME
        defaultStockItemShouldBeFound("productName.doesNotContain=" + UPDATED_PRODUCT_NAME);
    }

    @Test
    @Transactional
    void getAllStockItemsByImeiIsEqualToSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where imei equals to DEFAULT_IMEI
        defaultStockItemShouldBeFound("imei.equals=" + DEFAULT_IMEI);

        // Get all the stockItemList where imei equals to UPDATED_IMEI
        defaultStockItemShouldNotBeFound("imei.equals=" + UPDATED_IMEI);
    }

    @Test
    @Transactional
    void getAllStockItemsByImeiIsInShouldWork() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where imei in DEFAULT_IMEI or UPDATED_IMEI
        defaultStockItemShouldBeFound("imei.in=" + DEFAULT_IMEI + "," + UPDATED_IMEI);

        // Get all the stockItemList where imei equals to UPDATED_IMEI
        defaultStockItemShouldNotBeFound("imei.in=" + UPDATED_IMEI);
    }

    @Test
    @Transactional
    void getAllStockItemsByImeiIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where imei is not null
        defaultStockItemShouldBeFound("imei.specified=true");

        // Get all the stockItemList where imei is null
        defaultStockItemShouldNotBeFound("imei.specified=false");
    }

    @Test
    @Transactional
    void getAllStockItemsByImeiContainsSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where imei contains DEFAULT_IMEI
        defaultStockItemShouldBeFound("imei.contains=" + DEFAULT_IMEI);

        // Get all the stockItemList where imei contains UPDATED_IMEI
        defaultStockItemShouldNotBeFound("imei.contains=" + UPDATED_IMEI);
    }

    @Test
    @Transactional
    void getAllStockItemsByImeiNotContainsSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where imei does not contain DEFAULT_IMEI
        defaultStockItemShouldNotBeFound("imei.doesNotContain=" + DEFAULT_IMEI);

        // Get all the stockItemList where imei does not contain UPDATED_IMEI
        defaultStockItemShouldBeFound("imei.doesNotContain=" + UPDATED_IMEI);
    }

    @Test
    @Transactional
    void getAllStockItemsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where status equals to DEFAULT_STATUS
        defaultStockItemShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the stockItemList where status equals to UPDATED_STATUS
        defaultStockItemShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllStockItemsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultStockItemShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the stockItemList where status equals to UPDATED_STATUS
        defaultStockItemShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllStockItemsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where status is not null
        defaultStockItemShouldBeFound("status.specified=true");

        // Get all the stockItemList where status is null
        defaultStockItemShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllStockItemsByIsDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where isDeleted equals to DEFAULT_IS_DELETED
        defaultStockItemShouldBeFound("isDeleted.equals=" + DEFAULT_IS_DELETED);

        // Get all the stockItemList where isDeleted equals to UPDATED_IS_DELETED
        defaultStockItemShouldNotBeFound("isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllStockItemsByIsDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where isDeleted in DEFAULT_IS_DELETED or UPDATED_IS_DELETED
        defaultStockItemShouldBeFound("isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED);

        // Get all the stockItemList where isDeleted equals to UPDATED_IS_DELETED
        defaultStockItemShouldNotBeFound("isDeleted.in=" + UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void getAllStockItemsByIsDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where isDeleted is not null
        defaultStockItemShouldBeFound("isDeleted.specified=true");

        // Get all the stockItemList where isDeleted is null
        defaultStockItemShouldNotBeFound("isDeleted.specified=false");
    }

    @Test
    @Transactional
    void getAllStockItemsByAcquiredAtIsEqualToSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where acquiredAt equals to DEFAULT_ACQUIRED_AT
        defaultStockItemShouldBeFound("acquiredAt.equals=" + DEFAULT_ACQUIRED_AT);

        // Get all the stockItemList where acquiredAt equals to UPDATED_ACQUIRED_AT
        defaultStockItemShouldNotBeFound("acquiredAt.equals=" + UPDATED_ACQUIRED_AT);
    }

    @Test
    @Transactional
    void getAllStockItemsByAcquiredAtIsInShouldWork() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where acquiredAt in DEFAULT_ACQUIRED_AT or UPDATED_ACQUIRED_AT
        defaultStockItemShouldBeFound("acquiredAt.in=" + DEFAULT_ACQUIRED_AT + "," + UPDATED_ACQUIRED_AT);

        // Get all the stockItemList where acquiredAt equals to UPDATED_ACQUIRED_AT
        defaultStockItemShouldNotBeFound("acquiredAt.in=" + UPDATED_ACQUIRED_AT);
    }

    @Test
    @Transactional
    void getAllStockItemsByAcquiredAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where acquiredAt is not null
        defaultStockItemShouldBeFound("acquiredAt.specified=true");

        // Get all the stockItemList where acquiredAt is null
        defaultStockItemShouldNotBeFound("acquiredAt.specified=false");
    }

    @Test
    @Transactional
    void getAllStockItemsByAcquiredAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where acquiredAt is greater than or equal to DEFAULT_ACQUIRED_AT
        defaultStockItemShouldBeFound("acquiredAt.greaterThanOrEqual=" + DEFAULT_ACQUIRED_AT);

        // Get all the stockItemList where acquiredAt is greater than or equal to UPDATED_ACQUIRED_AT
        defaultStockItemShouldNotBeFound("acquiredAt.greaterThanOrEqual=" + UPDATED_ACQUIRED_AT);
    }

    @Test
    @Transactional
    void getAllStockItemsByAcquiredAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where acquiredAt is less than or equal to DEFAULT_ACQUIRED_AT
        defaultStockItemShouldBeFound("acquiredAt.lessThanOrEqual=" + DEFAULT_ACQUIRED_AT);

        // Get all the stockItemList where acquiredAt is less than or equal to SMALLER_ACQUIRED_AT
        defaultStockItemShouldNotBeFound("acquiredAt.lessThanOrEqual=" + SMALLER_ACQUIRED_AT);
    }

    @Test
    @Transactional
    void getAllStockItemsByAcquiredAtIsLessThanSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where acquiredAt is less than DEFAULT_ACQUIRED_AT
        defaultStockItemShouldNotBeFound("acquiredAt.lessThan=" + DEFAULT_ACQUIRED_AT);

        // Get all the stockItemList where acquiredAt is less than UPDATED_ACQUIRED_AT
        defaultStockItemShouldBeFound("acquiredAt.lessThan=" + UPDATED_ACQUIRED_AT);
    }

    @Test
    @Transactional
    void getAllStockItemsByAcquiredAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where acquiredAt is greater than DEFAULT_ACQUIRED_AT
        defaultStockItemShouldNotBeFound("acquiredAt.greaterThan=" + DEFAULT_ACQUIRED_AT);

        // Get all the stockItemList where acquiredAt is greater than SMALLER_ACQUIRED_AT
        defaultStockItemShouldBeFound("acquiredAt.greaterThan=" + SMALLER_ACQUIRED_AT);
    }

    @Test
    @Transactional
    void getAllStockItemsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where updatedAt equals to DEFAULT_UPDATED_AT
        defaultStockItemShouldBeFound("updatedAt.equals=" + DEFAULT_UPDATED_AT);

        // Get all the stockItemList where updatedAt equals to UPDATED_UPDATED_AT
        defaultStockItemShouldNotBeFound("updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllStockItemsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where updatedAt in DEFAULT_UPDATED_AT or UPDATED_UPDATED_AT
        defaultStockItemShouldBeFound("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT);

        // Get all the stockItemList where updatedAt equals to UPDATED_UPDATED_AT
        defaultStockItemShouldNotBeFound("updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllStockItemsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where updatedAt is not null
        defaultStockItemShouldBeFound("updatedAt.specified=true");

        // Get all the stockItemList where updatedAt is null
        defaultStockItemShouldNotBeFound("updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllStockItemsByUpdatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where updatedAt is greater than or equal to DEFAULT_UPDATED_AT
        defaultStockItemShouldBeFound("updatedAt.greaterThanOrEqual=" + DEFAULT_UPDATED_AT);

        // Get all the stockItemList where updatedAt is greater than or equal to UPDATED_UPDATED_AT
        defaultStockItemShouldNotBeFound("updatedAt.greaterThanOrEqual=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllStockItemsByUpdatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where updatedAt is less than or equal to DEFAULT_UPDATED_AT
        defaultStockItemShouldBeFound("updatedAt.lessThanOrEqual=" + DEFAULT_UPDATED_AT);

        // Get all the stockItemList where updatedAt is less than or equal to SMALLER_UPDATED_AT
        defaultStockItemShouldNotBeFound("updatedAt.lessThanOrEqual=" + SMALLER_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllStockItemsByUpdatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where updatedAt is less than DEFAULT_UPDATED_AT
        defaultStockItemShouldNotBeFound("updatedAt.lessThan=" + DEFAULT_UPDATED_AT);

        // Get all the stockItemList where updatedAt is less than UPDATED_UPDATED_AT
        defaultStockItemShouldBeFound("updatedAt.lessThan=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllStockItemsByUpdatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        // Get all the stockItemList where updatedAt is greater than DEFAULT_UPDATED_AT
        defaultStockItemShouldNotBeFound("updatedAt.greaterThan=" + DEFAULT_UPDATED_AT);

        // Get all the stockItemList where updatedAt is greater than SMALLER_UPDATED_AT
        defaultStockItemShouldBeFound("updatedAt.greaterThan=" + SMALLER_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllStockItemsByStockMovementsIsEqualToSomething() throws Exception {
        StockMovement stockMovements;
        if (TestUtil.findAll(em, StockMovement.class).isEmpty()) {
            stockItemRepository.saveAndFlush(stockItem);
            stockMovements = StockMovementResourceIT.createEntity(em);
        } else {
            stockMovements = TestUtil.findAll(em, StockMovement.class).get(0);
        }
        em.persist(stockMovements);
        em.flush();
        stockItem.addStockMovements(stockMovements);
        stockItemRepository.saveAndFlush(stockItem);
        Long stockMovementsId = stockMovements.getId();

        // Get all the stockItemList where stockMovements equals to stockMovementsId
        defaultStockItemShouldBeFound("stockMovementsId.equals=" + stockMovementsId);

        // Get all the stockItemList where stockMovements equals to (stockMovementsId + 1)
        defaultStockItemShouldNotBeFound("stockMovementsId.equals=" + (stockMovementsId + 1));
    }

    @Test
    @Transactional
    void getAllStockItemsByWarehouseIsEqualToSomething() throws Exception {
        Warehouse warehouse;
        if (TestUtil.findAll(em, Warehouse.class).isEmpty()) {
            stockItemRepository.saveAndFlush(stockItem);
            warehouse = WarehouseResourceIT.createEntity(em);
        } else {
            warehouse = TestUtil.findAll(em, Warehouse.class).get(0);
        }
        em.persist(warehouse);
        em.flush();
        stockItem.setWarehouse(warehouse);
        stockItemRepository.saveAndFlush(stockItem);
        Long warehouseId = warehouse.getId();

        // Get all the stockItemList where warehouse equals to warehouseId
        defaultStockItemShouldBeFound("warehouseId.equals=" + warehouseId);

        // Get all the stockItemList where warehouse equals to (warehouseId + 1)
        defaultStockItemShouldNotBeFound("warehouseId.equals=" + (warehouseId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStockItemShouldBeFound(String filter) throws Exception {
        restStockItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].tenantId").value(hasItem(DEFAULT_TENANT_ID.intValue())))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.intValue())))
            .andExpect(jsonPath("$.[*].productName").value(hasItem(DEFAULT_PRODUCT_NAME)))
            .andExpect(jsonPath("$.[*].imei").value(hasItem(DEFAULT_IMEI)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED.booleanValue())))
            .andExpect(jsonPath("$.[*].acquiredAt").value(hasItem(sameInstant(DEFAULT_ACQUIRED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))));

        // Check, that the count call also returns 1
        restStockItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStockItemShouldNotBeFound(String filter) throws Exception {
        restStockItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStockItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStockItem() throws Exception {
        // Get the stockItem
        restStockItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStockItem() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        int databaseSizeBeforeUpdate = stockItemRepository.findAll().size();

        // Update the stockItem
        StockItem updatedStockItem = stockItemRepository.findById(stockItem.getId()).get();
        // Disconnect from session so that the updates on updatedStockItem are not directly saved in db
        em.detach(updatedStockItem);
        updatedStockItem
            .tenantId(UPDATED_TENANT_ID)
            .productId(UPDATED_PRODUCT_ID)
            .productName(UPDATED_PRODUCT_NAME)
            .imei(UPDATED_IMEI)
            .status(UPDATED_STATUS)
            .isDeleted(UPDATED_IS_DELETED)
            .acquiredAt(UPDATED_ACQUIRED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        StockItemDTO stockItemDTO = stockItemMapper.toDto(updatedStockItem);

        restStockItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the StockItem in the database
        List<StockItem> stockItemList = stockItemRepository.findAll();
        assertThat(stockItemList).hasSize(databaseSizeBeforeUpdate);
        StockItem testStockItem = stockItemList.get(stockItemList.size() - 1);
        assertThat(testStockItem.getTenantId()).isEqualTo(UPDATED_TENANT_ID);
        assertThat(testStockItem.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testStockItem.getProductName()).isEqualTo(UPDATED_PRODUCT_NAME);
        assertThat(testStockItem.getImei()).isEqualTo(UPDATED_IMEI);
        assertThat(testStockItem.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testStockItem.getIsDeleted()).isEqualTo(UPDATED_IS_DELETED);
        assertThat(testStockItem.getAcquiredAt()).isEqualTo(UPDATED_ACQUIRED_AT);
        assertThat(testStockItem.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingStockItem() throws Exception {
        int databaseSizeBeforeUpdate = stockItemRepository.findAll().size();
        stockItem.setId(count.incrementAndGet());

        // Create the StockItem
        StockItemDTO stockItemDTO = stockItemMapper.toDto(stockItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockItem in the database
        List<StockItem> stockItemList = stockItemRepository.findAll();
        assertThat(stockItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStockItem() throws Exception {
        int databaseSizeBeforeUpdate = stockItemRepository.findAll().size();
        stockItem.setId(count.incrementAndGet());

        // Create the StockItem
        StockItemDTO stockItemDTO = stockItemMapper.toDto(stockItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockItem in the database
        List<StockItem> stockItemList = stockItemRepository.findAll();
        assertThat(stockItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStockItem() throws Exception {
        int databaseSizeBeforeUpdate = stockItemRepository.findAll().size();
        stockItem.setId(count.incrementAndGet());

        // Create the StockItem
        StockItemDTO stockItemDTO = stockItemMapper.toDto(stockItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockItem in the database
        List<StockItem> stockItemList = stockItemRepository.findAll();
        assertThat(stockItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStockItemWithPatch() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        int databaseSizeBeforeUpdate = stockItemRepository.findAll().size();

        // Update the stockItem using partial update
        StockItem partialUpdatedStockItem = new StockItem();
        partialUpdatedStockItem.setId(stockItem.getId());

        partialUpdatedStockItem
            .tenantId(UPDATED_TENANT_ID)
            .productName(UPDATED_PRODUCT_NAME)
            .imei(UPDATED_IMEI)
            .status(UPDATED_STATUS)
            .isDeleted(UPDATED_IS_DELETED);

        restStockItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStockItem))
            )
            .andExpect(status().isOk());

        // Validate the StockItem in the database
        List<StockItem> stockItemList = stockItemRepository.findAll();
        assertThat(stockItemList).hasSize(databaseSizeBeforeUpdate);
        StockItem testStockItem = stockItemList.get(stockItemList.size() - 1);
        assertThat(testStockItem.getTenantId()).isEqualTo(UPDATED_TENANT_ID);
        assertThat(testStockItem.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
        assertThat(testStockItem.getProductName()).isEqualTo(UPDATED_PRODUCT_NAME);
        assertThat(testStockItem.getImei()).isEqualTo(UPDATED_IMEI);
        assertThat(testStockItem.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testStockItem.getIsDeleted()).isEqualTo(UPDATED_IS_DELETED);
        assertThat(testStockItem.getAcquiredAt()).isEqualTo(DEFAULT_ACQUIRED_AT);
        assertThat(testStockItem.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    @Transactional
    void fullUpdateStockItemWithPatch() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        int databaseSizeBeforeUpdate = stockItemRepository.findAll().size();

        // Update the stockItem using partial update
        StockItem partialUpdatedStockItem = new StockItem();
        partialUpdatedStockItem.setId(stockItem.getId());

        partialUpdatedStockItem
            .tenantId(UPDATED_TENANT_ID)
            .productId(UPDATED_PRODUCT_ID)
            .productName(UPDATED_PRODUCT_NAME)
            .imei(UPDATED_IMEI)
            .status(UPDATED_STATUS)
            .isDeleted(UPDATED_IS_DELETED)
            .acquiredAt(UPDATED_ACQUIRED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restStockItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStockItem))
            )
            .andExpect(status().isOk());

        // Validate the StockItem in the database
        List<StockItem> stockItemList = stockItemRepository.findAll();
        assertThat(stockItemList).hasSize(databaseSizeBeforeUpdate);
        StockItem testStockItem = stockItemList.get(stockItemList.size() - 1);
        assertThat(testStockItem.getTenantId()).isEqualTo(UPDATED_TENANT_ID);
        assertThat(testStockItem.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testStockItem.getProductName()).isEqualTo(UPDATED_PRODUCT_NAME);
        assertThat(testStockItem.getImei()).isEqualTo(UPDATED_IMEI);
        assertThat(testStockItem.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testStockItem.getIsDeleted()).isEqualTo(UPDATED_IS_DELETED);
        assertThat(testStockItem.getAcquiredAt()).isEqualTo(UPDATED_ACQUIRED_AT);
        assertThat(testStockItem.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingStockItem() throws Exception {
        int databaseSizeBeforeUpdate = stockItemRepository.findAll().size();
        stockItem.setId(count.incrementAndGet());

        // Create the StockItem
        StockItemDTO stockItemDTO = stockItemMapper.toDto(stockItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stockItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockItem in the database
        List<StockItem> stockItemList = stockItemRepository.findAll();
        assertThat(stockItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStockItem() throws Exception {
        int databaseSizeBeforeUpdate = stockItemRepository.findAll().size();
        stockItem.setId(count.incrementAndGet());

        // Create the StockItem
        StockItemDTO stockItemDTO = stockItemMapper.toDto(stockItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockItem in the database
        List<StockItem> stockItemList = stockItemRepository.findAll();
        assertThat(stockItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStockItem() throws Exception {
        int databaseSizeBeforeUpdate = stockItemRepository.findAll().size();
        stockItem.setId(count.incrementAndGet());

        // Create the StockItem
        StockItemDTO stockItemDTO = stockItemMapper.toDto(stockItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockItemMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(stockItemDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockItem in the database
        List<StockItem> stockItemList = stockItemRepository.findAll();
        assertThat(stockItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStockItem() throws Exception {
        // Initialize the database
        stockItemRepository.saveAndFlush(stockItem);

        int databaseSizeBeforeDelete = stockItemRepository.findAll().size();

        // Delete the stockItem
        restStockItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, stockItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<StockItem> stockItemList = stockItemRepository.findAll();
        assertThat(stockItemList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
