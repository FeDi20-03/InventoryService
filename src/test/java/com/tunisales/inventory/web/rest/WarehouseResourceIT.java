package com.tunisales.inventory.web.rest;

import static com.tunisales.inventory.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tunisales.inventory.IntegrationTest;
import com.tunisales.inventory.domain.StockItem;
import com.tunisales.inventory.domain.Warehouse;
import com.tunisales.inventory.domain.enumeration.WarehouseType;
import com.tunisales.inventory.repository.WarehouseRepository;
import com.tunisales.inventory.service.criteria.WarehouseCriteria;
import com.tunisales.inventory.service.dto.WarehouseDTO;
import com.tunisales.inventory.service.mapper.WarehouseMapper;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link WarehouseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WarehouseResourceIT {

    private static final Long DEFAULT_TENANT_ID = 1L;
    private static final Long UPDATED_TENANT_ID = 2L;
    private static final Long SMALLER_TENANT_ID = 1L - 1L;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final WarehouseType DEFAULT_TYPE = WarehouseType.LOCAL;
    private static final WarehouseType UPDATED_TYPE = WarehouseType.SITE;

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final Integer DEFAULT_MIN_THRESHOLD = 0;
    private static final Integer UPDATED_MIN_THRESHOLD = 1;
    private static final Integer SMALLER_MIN_THRESHOLD = 0 - 1;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/warehouses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private WarehouseMapper warehouseMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWarehouseMockMvc;

    private Warehouse warehouse;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Warehouse createEntity(EntityManager em) {
        Warehouse warehouse = new Warehouse()
            .tenantId(DEFAULT_TENANT_ID)
            .name(DEFAULT_NAME)
            .type(DEFAULT_TYPE)
            .address(DEFAULT_ADDRESS)
            .city(DEFAULT_CITY)
            .minThreshold(DEFAULT_MIN_THRESHOLD)
            .isActive(DEFAULT_IS_ACTIVE)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        return warehouse;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Warehouse createUpdatedEntity(EntityManager em) {
        Warehouse warehouse = new Warehouse()
            .tenantId(UPDATED_TENANT_ID)
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .address(UPDATED_ADDRESS)
            .city(UPDATED_CITY)
            .minThreshold(UPDATED_MIN_THRESHOLD)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        return warehouse;
    }

    @BeforeEach
    public void initTest() {
        warehouse = createEntity(em);
    }

    @Test
    @Transactional
    void createWarehouse() throws Exception {
        int databaseSizeBeforeCreate = warehouseRepository.findAll().size();
        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);
        restWarehouseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(warehouseDTO)))
            .andExpect(status().isCreated());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeCreate + 1);
        Warehouse testWarehouse = warehouseList.get(warehouseList.size() - 1);
        assertThat(testWarehouse.getTenantId()).isEqualTo(DEFAULT_TENANT_ID);
        assertThat(testWarehouse.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testWarehouse.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testWarehouse.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testWarehouse.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testWarehouse.getMinThreshold()).isEqualTo(DEFAULT_MIN_THRESHOLD);
        assertThat(testWarehouse.getIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);
        assertThat(testWarehouse.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testWarehouse.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    @Transactional
    void createWarehouseWithExistingId() throws Exception {
        // Create the Warehouse with an existing ID
        warehouse.setId(1L);
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        int databaseSizeBeforeCreate = warehouseRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWarehouseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(warehouseDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTenantIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = warehouseRepository.findAll().size();
        // set the field null
        warehouse.setTenantId(null);

        // Create the Warehouse, which fails.
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        restWarehouseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(warehouseDTO)))
            .andExpect(status().isBadRequest());

        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = warehouseRepository.findAll().size();
        // set the field null
        warehouse.setName(null);

        // Create the Warehouse, which fails.
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        restWarehouseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(warehouseDTO)))
            .andExpect(status().isBadRequest());

        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = warehouseRepository.findAll().size();
        // set the field null
        warehouse.setType(null);

        // Create the Warehouse, which fails.
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        restWarehouseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(warehouseDTO)))
            .andExpect(status().isBadRequest());

        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = warehouseRepository.findAll().size();
        // set the field null
        warehouse.setIsActive(null);

        // Create the Warehouse, which fails.
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        restWarehouseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(warehouseDTO)))
            .andExpect(status().isBadRequest());

        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = warehouseRepository.findAll().size();
        // set the field null
        warehouse.setCreatedAt(null);

        // Create the Warehouse, which fails.
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        restWarehouseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(warehouseDTO)))
            .andExpect(status().isBadRequest());

        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllWarehouses() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList
        restWarehouseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(warehouse.getId().intValue())))
            .andExpect(jsonPath("$.[*].tenantId").value(hasItem(DEFAULT_TENANT_ID.intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].minThreshold").value(hasItem(DEFAULT_MIN_THRESHOLD)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))));
    }

    @Test
    @Transactional
    void getWarehouse() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get the warehouse
        restWarehouseMockMvc
            .perform(get(ENTITY_API_URL_ID, warehouse.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(warehouse.getId().intValue()))
            .andExpect(jsonPath("$.tenantId").value(DEFAULT_TENANT_ID.intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.minThreshold").value(DEFAULT_MIN_THRESHOLD))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)))
            .andExpect(jsonPath("$.updatedAt").value(sameInstant(DEFAULT_UPDATED_AT)));
    }

    @Test
    @Transactional
    void getWarehousesByIdFiltering() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        Long id = warehouse.getId();

        defaultWarehouseShouldBeFound("id.equals=" + id);
        defaultWarehouseShouldNotBeFound("id.notEquals=" + id);

        defaultWarehouseShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultWarehouseShouldNotBeFound("id.greaterThan=" + id);

        defaultWarehouseShouldBeFound("id.lessThanOrEqual=" + id);
        defaultWarehouseShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllWarehousesByTenantIdIsEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where tenantId equals to DEFAULT_TENANT_ID
        defaultWarehouseShouldBeFound("tenantId.equals=" + DEFAULT_TENANT_ID);

        // Get all the warehouseList where tenantId equals to UPDATED_TENANT_ID
        defaultWarehouseShouldNotBeFound("tenantId.equals=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllWarehousesByTenantIdIsInShouldWork() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where tenantId in DEFAULT_TENANT_ID or UPDATED_TENANT_ID
        defaultWarehouseShouldBeFound("tenantId.in=" + DEFAULT_TENANT_ID + "," + UPDATED_TENANT_ID);

        // Get all the warehouseList where tenantId equals to UPDATED_TENANT_ID
        defaultWarehouseShouldNotBeFound("tenantId.in=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllWarehousesByTenantIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where tenantId is not null
        defaultWarehouseShouldBeFound("tenantId.specified=true");

        // Get all the warehouseList where tenantId is null
        defaultWarehouseShouldNotBeFound("tenantId.specified=false");
    }

    @Test
    @Transactional
    void getAllWarehousesByTenantIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where tenantId is greater than or equal to DEFAULT_TENANT_ID
        defaultWarehouseShouldBeFound("tenantId.greaterThanOrEqual=" + DEFAULT_TENANT_ID);

        // Get all the warehouseList where tenantId is greater than or equal to UPDATED_TENANT_ID
        defaultWarehouseShouldNotBeFound("tenantId.greaterThanOrEqual=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllWarehousesByTenantIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where tenantId is less than or equal to DEFAULT_TENANT_ID
        defaultWarehouseShouldBeFound("tenantId.lessThanOrEqual=" + DEFAULT_TENANT_ID);

        // Get all the warehouseList where tenantId is less than or equal to SMALLER_TENANT_ID
        defaultWarehouseShouldNotBeFound("tenantId.lessThanOrEqual=" + SMALLER_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllWarehousesByTenantIdIsLessThanSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where tenantId is less than DEFAULT_TENANT_ID
        defaultWarehouseShouldNotBeFound("tenantId.lessThan=" + DEFAULT_TENANT_ID);

        // Get all the warehouseList where tenantId is less than UPDATED_TENANT_ID
        defaultWarehouseShouldBeFound("tenantId.lessThan=" + UPDATED_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllWarehousesByTenantIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where tenantId is greater than DEFAULT_TENANT_ID
        defaultWarehouseShouldNotBeFound("tenantId.greaterThan=" + DEFAULT_TENANT_ID);

        // Get all the warehouseList where tenantId is greater than SMALLER_TENANT_ID
        defaultWarehouseShouldBeFound("tenantId.greaterThan=" + SMALLER_TENANT_ID);
    }

    @Test
    @Transactional
    void getAllWarehousesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where name equals to DEFAULT_NAME
        defaultWarehouseShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the warehouseList where name equals to UPDATED_NAME
        defaultWarehouseShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWarehousesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where name in DEFAULT_NAME or UPDATED_NAME
        defaultWarehouseShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the warehouseList where name equals to UPDATED_NAME
        defaultWarehouseShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWarehousesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where name is not null
        defaultWarehouseShouldBeFound("name.specified=true");

        // Get all the warehouseList where name is null
        defaultWarehouseShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllWarehousesByNameContainsSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where name contains DEFAULT_NAME
        defaultWarehouseShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the warehouseList where name contains UPDATED_NAME
        defaultWarehouseShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWarehousesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where name does not contain DEFAULT_NAME
        defaultWarehouseShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the warehouseList where name does not contain UPDATED_NAME
        defaultWarehouseShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWarehousesByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where type equals to DEFAULT_TYPE
        defaultWarehouseShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the warehouseList where type equals to UPDATED_TYPE
        defaultWarehouseShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllWarehousesByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultWarehouseShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the warehouseList where type equals to UPDATED_TYPE
        defaultWarehouseShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllWarehousesByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where type is not null
        defaultWarehouseShouldBeFound("type.specified=true");

        // Get all the warehouseList where type is null
        defaultWarehouseShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    void getAllWarehousesByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where address equals to DEFAULT_ADDRESS
        defaultWarehouseShouldBeFound("address.equals=" + DEFAULT_ADDRESS);

        // Get all the warehouseList where address equals to UPDATED_ADDRESS
        defaultWarehouseShouldNotBeFound("address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllWarehousesByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where address in DEFAULT_ADDRESS or UPDATED_ADDRESS
        defaultWarehouseShouldBeFound("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS);

        // Get all the warehouseList where address equals to UPDATED_ADDRESS
        defaultWarehouseShouldNotBeFound("address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllWarehousesByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where address is not null
        defaultWarehouseShouldBeFound("address.specified=true");

        // Get all the warehouseList where address is null
        defaultWarehouseShouldNotBeFound("address.specified=false");
    }

    @Test
    @Transactional
    void getAllWarehousesByAddressContainsSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where address contains DEFAULT_ADDRESS
        defaultWarehouseShouldBeFound("address.contains=" + DEFAULT_ADDRESS);

        // Get all the warehouseList where address contains UPDATED_ADDRESS
        defaultWarehouseShouldNotBeFound("address.contains=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllWarehousesByAddressNotContainsSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where address does not contain DEFAULT_ADDRESS
        defaultWarehouseShouldNotBeFound("address.doesNotContain=" + DEFAULT_ADDRESS);

        // Get all the warehouseList where address does not contain UPDATED_ADDRESS
        defaultWarehouseShouldBeFound("address.doesNotContain=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllWarehousesByCityIsEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where city equals to DEFAULT_CITY
        defaultWarehouseShouldBeFound("city.equals=" + DEFAULT_CITY);

        // Get all the warehouseList where city equals to UPDATED_CITY
        defaultWarehouseShouldNotBeFound("city.equals=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllWarehousesByCityIsInShouldWork() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where city in DEFAULT_CITY or UPDATED_CITY
        defaultWarehouseShouldBeFound("city.in=" + DEFAULT_CITY + "," + UPDATED_CITY);

        // Get all the warehouseList where city equals to UPDATED_CITY
        defaultWarehouseShouldNotBeFound("city.in=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllWarehousesByCityIsNullOrNotNull() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where city is not null
        defaultWarehouseShouldBeFound("city.specified=true");

        // Get all the warehouseList where city is null
        defaultWarehouseShouldNotBeFound("city.specified=false");
    }

    @Test
    @Transactional
    void getAllWarehousesByCityContainsSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where city contains DEFAULT_CITY
        defaultWarehouseShouldBeFound("city.contains=" + DEFAULT_CITY);

        // Get all the warehouseList where city contains UPDATED_CITY
        defaultWarehouseShouldNotBeFound("city.contains=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllWarehousesByCityNotContainsSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where city does not contain DEFAULT_CITY
        defaultWarehouseShouldNotBeFound("city.doesNotContain=" + DEFAULT_CITY);

        // Get all the warehouseList where city does not contain UPDATED_CITY
        defaultWarehouseShouldBeFound("city.doesNotContain=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllWarehousesByMinThresholdIsEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where minThreshold equals to DEFAULT_MIN_THRESHOLD
        defaultWarehouseShouldBeFound("minThreshold.equals=" + DEFAULT_MIN_THRESHOLD);

        // Get all the warehouseList where minThreshold equals to UPDATED_MIN_THRESHOLD
        defaultWarehouseShouldNotBeFound("minThreshold.equals=" + UPDATED_MIN_THRESHOLD);
    }

    @Test
    @Transactional
    void getAllWarehousesByMinThresholdIsInShouldWork() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where minThreshold in DEFAULT_MIN_THRESHOLD or UPDATED_MIN_THRESHOLD
        defaultWarehouseShouldBeFound("minThreshold.in=" + DEFAULT_MIN_THRESHOLD + "," + UPDATED_MIN_THRESHOLD);

        // Get all the warehouseList where minThreshold equals to UPDATED_MIN_THRESHOLD
        defaultWarehouseShouldNotBeFound("minThreshold.in=" + UPDATED_MIN_THRESHOLD);
    }

    @Test
    @Transactional
    void getAllWarehousesByMinThresholdIsNullOrNotNull() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where minThreshold is not null
        defaultWarehouseShouldBeFound("minThreshold.specified=true");

        // Get all the warehouseList where minThreshold is null
        defaultWarehouseShouldNotBeFound("minThreshold.specified=false");
    }

    @Test
    @Transactional
    void getAllWarehousesByMinThresholdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where minThreshold is greater than or equal to DEFAULT_MIN_THRESHOLD
        defaultWarehouseShouldBeFound("minThreshold.greaterThanOrEqual=" + DEFAULT_MIN_THRESHOLD);

        // Get all the warehouseList where minThreshold is greater than or equal to UPDATED_MIN_THRESHOLD
        defaultWarehouseShouldNotBeFound("minThreshold.greaterThanOrEqual=" + UPDATED_MIN_THRESHOLD);
    }

    @Test
    @Transactional
    void getAllWarehousesByMinThresholdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where minThreshold is less than or equal to DEFAULT_MIN_THRESHOLD
        defaultWarehouseShouldBeFound("minThreshold.lessThanOrEqual=" + DEFAULT_MIN_THRESHOLD);

        // Get all the warehouseList where minThreshold is less than or equal to SMALLER_MIN_THRESHOLD
        defaultWarehouseShouldNotBeFound("minThreshold.lessThanOrEqual=" + SMALLER_MIN_THRESHOLD);
    }

    @Test
    @Transactional
    void getAllWarehousesByMinThresholdIsLessThanSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where minThreshold is less than DEFAULT_MIN_THRESHOLD
        defaultWarehouseShouldNotBeFound("minThreshold.lessThan=" + DEFAULT_MIN_THRESHOLD);

        // Get all the warehouseList where minThreshold is less than UPDATED_MIN_THRESHOLD
        defaultWarehouseShouldBeFound("minThreshold.lessThan=" + UPDATED_MIN_THRESHOLD);
    }

    @Test
    @Transactional
    void getAllWarehousesByMinThresholdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where minThreshold is greater than DEFAULT_MIN_THRESHOLD
        defaultWarehouseShouldNotBeFound("minThreshold.greaterThan=" + DEFAULT_MIN_THRESHOLD);

        // Get all the warehouseList where minThreshold is greater than SMALLER_MIN_THRESHOLD
        defaultWarehouseShouldBeFound("minThreshold.greaterThan=" + SMALLER_MIN_THRESHOLD);
    }

    @Test
    @Transactional
    void getAllWarehousesByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where isActive equals to DEFAULT_IS_ACTIVE
        defaultWarehouseShouldBeFound("isActive.equals=" + DEFAULT_IS_ACTIVE);

        // Get all the warehouseList where isActive equals to UPDATED_IS_ACTIVE
        defaultWarehouseShouldNotBeFound("isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllWarehousesByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where isActive in DEFAULT_IS_ACTIVE or UPDATED_IS_ACTIVE
        defaultWarehouseShouldBeFound("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE);

        // Get all the warehouseList where isActive equals to UPDATED_IS_ACTIVE
        defaultWarehouseShouldNotBeFound("isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllWarehousesByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where isActive is not null
        defaultWarehouseShouldBeFound("isActive.specified=true");

        // Get all the warehouseList where isActive is null
        defaultWarehouseShouldNotBeFound("isActive.specified=false");
    }

    @Test
    @Transactional
    void getAllWarehousesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where createdAt equals to DEFAULT_CREATED_AT
        defaultWarehouseShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the warehouseList where createdAt equals to UPDATED_CREATED_AT
        defaultWarehouseShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllWarehousesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultWarehouseShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the warehouseList where createdAt equals to UPDATED_CREATED_AT
        defaultWarehouseShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllWarehousesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where createdAt is not null
        defaultWarehouseShouldBeFound("createdAt.specified=true");

        // Get all the warehouseList where createdAt is null
        defaultWarehouseShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllWarehousesByCreatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where createdAt is greater than or equal to DEFAULT_CREATED_AT
        defaultWarehouseShouldBeFound("createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the warehouseList where createdAt is greater than or equal to UPDATED_CREATED_AT
        defaultWarehouseShouldNotBeFound("createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllWarehousesByCreatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where createdAt is less than or equal to DEFAULT_CREATED_AT
        defaultWarehouseShouldBeFound("createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT);

        // Get all the warehouseList where createdAt is less than or equal to SMALLER_CREATED_AT
        defaultWarehouseShouldNotBeFound("createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllWarehousesByCreatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where createdAt is less than DEFAULT_CREATED_AT
        defaultWarehouseShouldNotBeFound("createdAt.lessThan=" + DEFAULT_CREATED_AT);

        // Get all the warehouseList where createdAt is less than UPDATED_CREATED_AT
        defaultWarehouseShouldBeFound("createdAt.lessThan=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllWarehousesByCreatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where createdAt is greater than DEFAULT_CREATED_AT
        defaultWarehouseShouldNotBeFound("createdAt.greaterThan=" + DEFAULT_CREATED_AT);

        // Get all the warehouseList where createdAt is greater than SMALLER_CREATED_AT
        defaultWarehouseShouldBeFound("createdAt.greaterThan=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllWarehousesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where updatedAt equals to DEFAULT_UPDATED_AT
        defaultWarehouseShouldBeFound("updatedAt.equals=" + DEFAULT_UPDATED_AT);

        // Get all the warehouseList where updatedAt equals to UPDATED_UPDATED_AT
        defaultWarehouseShouldNotBeFound("updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllWarehousesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where updatedAt in DEFAULT_UPDATED_AT or UPDATED_UPDATED_AT
        defaultWarehouseShouldBeFound("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT);

        // Get all the warehouseList where updatedAt equals to UPDATED_UPDATED_AT
        defaultWarehouseShouldNotBeFound("updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllWarehousesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where updatedAt is not null
        defaultWarehouseShouldBeFound("updatedAt.specified=true");

        // Get all the warehouseList where updatedAt is null
        defaultWarehouseShouldNotBeFound("updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllWarehousesByUpdatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where updatedAt is greater than or equal to DEFAULT_UPDATED_AT
        defaultWarehouseShouldBeFound("updatedAt.greaterThanOrEqual=" + DEFAULT_UPDATED_AT);

        // Get all the warehouseList where updatedAt is greater than or equal to UPDATED_UPDATED_AT
        defaultWarehouseShouldNotBeFound("updatedAt.greaterThanOrEqual=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllWarehousesByUpdatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where updatedAt is less than or equal to DEFAULT_UPDATED_AT
        defaultWarehouseShouldBeFound("updatedAt.lessThanOrEqual=" + DEFAULT_UPDATED_AT);

        // Get all the warehouseList where updatedAt is less than or equal to SMALLER_UPDATED_AT
        defaultWarehouseShouldNotBeFound("updatedAt.lessThanOrEqual=" + SMALLER_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllWarehousesByUpdatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where updatedAt is less than DEFAULT_UPDATED_AT
        defaultWarehouseShouldNotBeFound("updatedAt.lessThan=" + DEFAULT_UPDATED_AT);

        // Get all the warehouseList where updatedAt is less than UPDATED_UPDATED_AT
        defaultWarehouseShouldBeFound("updatedAt.lessThan=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllWarehousesByUpdatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where updatedAt is greater than DEFAULT_UPDATED_AT
        defaultWarehouseShouldNotBeFound("updatedAt.greaterThan=" + DEFAULT_UPDATED_AT);

        // Get all the warehouseList where updatedAt is greater than SMALLER_UPDATED_AT
        defaultWarehouseShouldBeFound("updatedAt.greaterThan=" + SMALLER_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllWarehousesByStockItemsIsEqualToSomething() throws Exception {
        StockItem stockItems;
        if (TestUtil.findAll(em, StockItem.class).isEmpty()) {
            warehouseRepository.saveAndFlush(warehouse);
            stockItems = StockItemResourceIT.createEntity(em);
        } else {
            stockItems = TestUtil.findAll(em, StockItem.class).get(0);
        }
        em.persist(stockItems);
        em.flush();
        warehouse.addStockItems(stockItems);
        warehouseRepository.saveAndFlush(warehouse);
        Long stockItemsId = stockItems.getId();

        // Get all the warehouseList where stockItems equals to stockItemsId
        defaultWarehouseShouldBeFound("stockItemsId.equals=" + stockItemsId);

        // Get all the warehouseList where stockItems equals to (stockItemsId + 1)
        defaultWarehouseShouldNotBeFound("stockItemsId.equals=" + (stockItemsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWarehouseShouldBeFound(String filter) throws Exception {
        restWarehouseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(warehouse.getId().intValue())))
            .andExpect(jsonPath("$.[*].tenantId").value(hasItem(DEFAULT_TENANT_ID.intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].minThreshold").value(hasItem(DEFAULT_MIN_THRESHOLD)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))));

        // Check, that the count call also returns 1
        restWarehouseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWarehouseShouldNotBeFound(String filter) throws Exception {
        restWarehouseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWarehouseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingWarehouse() throws Exception {
        // Get the warehouse
        restWarehouseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWarehouse() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        int databaseSizeBeforeUpdate = warehouseRepository.findAll().size();

        // Update the warehouse
        Warehouse updatedWarehouse = warehouseRepository.findById(warehouse.getId()).get();
        // Disconnect from session so that the updates on updatedWarehouse are not directly saved in db
        em.detach(updatedWarehouse);
        updatedWarehouse
            .tenantId(UPDATED_TENANT_ID)
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .address(UPDATED_ADDRESS)
            .city(UPDATED_CITY)
            .minThreshold(UPDATED_MIN_THRESHOLD)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(updatedWarehouse);

        restWarehouseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, warehouseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(warehouseDTO))
            )
            .andExpect(status().isOk());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeUpdate);
        Warehouse testWarehouse = warehouseList.get(warehouseList.size() - 1);
        assertThat(testWarehouse.getTenantId()).isEqualTo(UPDATED_TENANT_ID);
        assertThat(testWarehouse.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testWarehouse.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testWarehouse.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testWarehouse.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testWarehouse.getMinThreshold()).isEqualTo(UPDATED_MIN_THRESHOLD);
        assertThat(testWarehouse.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
        assertThat(testWarehouse.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testWarehouse.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingWarehouse() throws Exception {
        int databaseSizeBeforeUpdate = warehouseRepository.findAll().size();
        warehouse.setId(count.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, warehouseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(warehouseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWarehouse() throws Exception {
        int databaseSizeBeforeUpdate = warehouseRepository.findAll().size();
        warehouse.setId(count.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(warehouseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWarehouse() throws Exception {
        int databaseSizeBeforeUpdate = warehouseRepository.findAll().size();
        warehouse.setId(count.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(warehouseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWarehouseWithPatch() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        int databaseSizeBeforeUpdate = warehouseRepository.findAll().size();

        // Update the warehouse using partial update
        Warehouse partialUpdatedWarehouse = new Warehouse();
        partialUpdatedWarehouse.setId(warehouse.getId());

        partialUpdatedWarehouse.name(UPDATED_NAME);

        restWarehouseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWarehouse.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWarehouse))
            )
            .andExpect(status().isOk());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeUpdate);
        Warehouse testWarehouse = warehouseList.get(warehouseList.size() - 1);
        assertThat(testWarehouse.getTenantId()).isEqualTo(DEFAULT_TENANT_ID);
        assertThat(testWarehouse.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testWarehouse.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testWarehouse.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testWarehouse.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testWarehouse.getMinThreshold()).isEqualTo(DEFAULT_MIN_THRESHOLD);
        assertThat(testWarehouse.getIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);
        assertThat(testWarehouse.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testWarehouse.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    @Transactional
    void fullUpdateWarehouseWithPatch() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        int databaseSizeBeforeUpdate = warehouseRepository.findAll().size();

        // Update the warehouse using partial update
        Warehouse partialUpdatedWarehouse = new Warehouse();
        partialUpdatedWarehouse.setId(warehouse.getId());

        partialUpdatedWarehouse
            .tenantId(UPDATED_TENANT_ID)
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .address(UPDATED_ADDRESS)
            .city(UPDATED_CITY)
            .minThreshold(UPDATED_MIN_THRESHOLD)
            .isActive(UPDATED_IS_ACTIVE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restWarehouseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWarehouse.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWarehouse))
            )
            .andExpect(status().isOk());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeUpdate);
        Warehouse testWarehouse = warehouseList.get(warehouseList.size() - 1);
        assertThat(testWarehouse.getTenantId()).isEqualTo(UPDATED_TENANT_ID);
        assertThat(testWarehouse.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testWarehouse.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testWarehouse.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testWarehouse.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testWarehouse.getMinThreshold()).isEqualTo(UPDATED_MIN_THRESHOLD);
        assertThat(testWarehouse.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
        assertThat(testWarehouse.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testWarehouse.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingWarehouse() throws Exception {
        int databaseSizeBeforeUpdate = warehouseRepository.findAll().size();
        warehouse.setId(count.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, warehouseDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(warehouseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWarehouse() throws Exception {
        int databaseSizeBeforeUpdate = warehouseRepository.findAll().size();
        warehouse.setId(count.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(warehouseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWarehouse() throws Exception {
        int databaseSizeBeforeUpdate = warehouseRepository.findAll().size();
        warehouse.setId(count.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(warehouseDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWarehouse() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        int databaseSizeBeforeDelete = warehouseRepository.findAll().size();

        // Delete the warehouse
        restWarehouseMockMvc
            .perform(delete(ENTITY_API_URL_ID, warehouse.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
