package de.aktienmathematik.web.rest;

import de.aktienmathematik.AktienmathematikApp;
import de.aktienmathematik.domain.Aktien;
import de.aktienmathematik.repository.AktienRepository;
import de.aktienmathematik.service.AktienService;
import de.aktienmathematik.web.rest.errors.ExceptionTranslator;
import de.aktienmathematik.service.dto.AktienCriteria;
import de.aktienmathematik.service.AktienQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static de.aktienmathematik.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link AktienResource} REST controller.
 */
@SpringBootTest(classes = AktienmathematikApp.class)
public class AktienResourceIT {

    private static final String DEFAULT_SYMBOL = "AAAAAAAAAA";
    private static final String UPDATED_SYMBOL = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Double DEFAULT_OPEN = 1D;
    private static final Double UPDATED_OPEN = 2D;
    private static final Double SMALLER_OPEN = 1D - 1D;

    private static final Double DEFAULT_CLOSE = 1D;
    private static final Double UPDATED_CLOSE = 2D;
    private static final Double SMALLER_CLOSE = 1D - 1D;

    private static final Double DEFAULT_HIGH = 1D;
    private static final Double UPDATED_HIGH = 2D;
    private static final Double SMALLER_HIGH = 1D - 1D;

    private static final Double DEFAULT_LOW = 1D;
    private static final Double UPDATED_LOW = 2D;
    private static final Double SMALLER_LOW = 1D - 1D;

    private static final Integer DEFAULT_VOLUME = 1;
    private static final Integer UPDATED_VOLUME = 2;
    private static final Integer SMALLER_VOLUME = 1 - 1;

    @Autowired
    private AktienRepository aktienRepository;

    @Autowired
    private AktienService aktienService;

    @Autowired
    private AktienQueryService aktienQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restAktienMockMvc;

    private Aktien aktien;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AktienResource aktienResource = new AktienResource(aktienService, aktienQueryService);
        this.restAktienMockMvc = MockMvcBuilders.standaloneSetup(aktienResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Aktien createEntity(EntityManager em) {
        Aktien aktien = new Aktien()
            .symbol(DEFAULT_SYMBOL)
            .date(DEFAULT_DATE)
            .open(DEFAULT_OPEN)
            .close(DEFAULT_CLOSE)
            .high(DEFAULT_HIGH)
            .low(DEFAULT_LOW)
            .volume(DEFAULT_VOLUME);
        return aktien;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Aktien createUpdatedEntity(EntityManager em) {
        Aktien aktien = new Aktien()
            .symbol(UPDATED_SYMBOL)
            .date(UPDATED_DATE)
            .open(UPDATED_OPEN)
            .close(UPDATED_CLOSE)
            .high(UPDATED_HIGH)
            .low(UPDATED_LOW)
            .volume(UPDATED_VOLUME);
        return aktien;
    }

    @BeforeEach
    public void initTest() {
        aktien = createEntity(em);
    }

    @Test
    @Transactional
    public void createAktien() throws Exception {
        int databaseSizeBeforeCreate = aktienRepository.findAll().size();

        // Create the Aktien
        restAktienMockMvc.perform(post("/api/aktiens")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(aktien)))
            .andExpect(status().isCreated());

        // Validate the Aktien in the database
        List<Aktien> aktienList = aktienRepository.findAll();
        assertThat(aktienList).hasSize(databaseSizeBeforeCreate + 1);
        Aktien testAktien = aktienList.get(aktienList.size() - 1);
        assertThat(testAktien.getSymbol()).isEqualTo(DEFAULT_SYMBOL);
        assertThat(testAktien.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testAktien.getOpen()).isEqualTo(DEFAULT_OPEN);
        assertThat(testAktien.getClose()).isEqualTo(DEFAULT_CLOSE);
        assertThat(testAktien.getHigh()).isEqualTo(DEFAULT_HIGH);
        assertThat(testAktien.getLow()).isEqualTo(DEFAULT_LOW);
        assertThat(testAktien.getVolume()).isEqualTo(DEFAULT_VOLUME);
    }

    @Test
    @Transactional
    public void createAktienWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = aktienRepository.findAll().size();

        // Create the Aktien with an existing ID
        aktien.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAktienMockMvc.perform(post("/api/aktiens")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(aktien)))
            .andExpect(status().isBadRequest());

        // Validate the Aktien in the database
        List<Aktien> aktienList = aktienRepository.findAll();
        assertThat(aktienList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllAktiens() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList
        restAktienMockMvc.perform(get("/api/aktiens?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aktien.getId().intValue())))
            .andExpect(jsonPath("$.[*].symbol").value(hasItem(DEFAULT_SYMBOL)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].open").value(hasItem(DEFAULT_OPEN.doubleValue())))
            .andExpect(jsonPath("$.[*].close").value(hasItem(DEFAULT_CLOSE.doubleValue())))
            .andExpect(jsonPath("$.[*].high").value(hasItem(DEFAULT_HIGH.doubleValue())))
            .andExpect(jsonPath("$.[*].low").value(hasItem(DEFAULT_LOW.doubleValue())))
            .andExpect(jsonPath("$.[*].volume").value(hasItem(DEFAULT_VOLUME)));
    }
    
    @Test
    @Transactional
    public void getAktien() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get the aktien
        restAktienMockMvc.perform(get("/api/aktiens/{id}", aktien.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(aktien.getId().intValue()))
            .andExpect(jsonPath("$.symbol").value(DEFAULT_SYMBOL))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.open").value(DEFAULT_OPEN.doubleValue()))
            .andExpect(jsonPath("$.close").value(DEFAULT_CLOSE.doubleValue()))
            .andExpect(jsonPath("$.high").value(DEFAULT_HIGH.doubleValue()))
            .andExpect(jsonPath("$.low").value(DEFAULT_LOW.doubleValue()))
            .andExpect(jsonPath("$.volume").value(DEFAULT_VOLUME));
    }


    @Test
    @Transactional
    public void getAktiensByIdFiltering() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        Long id = aktien.getId();

        defaultAktienShouldBeFound("id.equals=" + id);
        defaultAktienShouldNotBeFound("id.notEquals=" + id);

        defaultAktienShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultAktienShouldNotBeFound("id.greaterThan=" + id);

        defaultAktienShouldBeFound("id.lessThanOrEqual=" + id);
        defaultAktienShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllAktiensBySymbolIsEqualToSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where symbol equals to DEFAULT_SYMBOL
        defaultAktienShouldBeFound("symbol.equals=" + DEFAULT_SYMBOL);

        // Get all the aktienList where symbol equals to UPDATED_SYMBOL
        defaultAktienShouldNotBeFound("symbol.equals=" + UPDATED_SYMBOL);
    }

    @Test
    @Transactional
    public void getAllAktiensBySymbolIsNotEqualToSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where symbol not equals to DEFAULT_SYMBOL
        defaultAktienShouldNotBeFound("symbol.notEquals=" + DEFAULT_SYMBOL);

        // Get all the aktienList where symbol not equals to UPDATED_SYMBOL
        defaultAktienShouldBeFound("symbol.notEquals=" + UPDATED_SYMBOL);
    }

    @Test
    @Transactional
    public void getAllAktiensBySymbolIsInShouldWork() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where symbol in DEFAULT_SYMBOL or UPDATED_SYMBOL
        defaultAktienShouldBeFound("symbol.in=" + DEFAULT_SYMBOL + "," + UPDATED_SYMBOL);

        // Get all the aktienList where symbol equals to UPDATED_SYMBOL
        defaultAktienShouldNotBeFound("symbol.in=" + UPDATED_SYMBOL);
    }

    @Test
    @Transactional
    public void getAllAktiensBySymbolIsNullOrNotNull() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where symbol is not null
        defaultAktienShouldBeFound("symbol.specified=true");

        // Get all the aktienList where symbol is null
        defaultAktienShouldNotBeFound("symbol.specified=false");
    }
                @Test
    @Transactional
    public void getAllAktiensBySymbolContainsSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where symbol contains DEFAULT_SYMBOL
        defaultAktienShouldBeFound("symbol.contains=" + DEFAULT_SYMBOL);

        // Get all the aktienList where symbol contains UPDATED_SYMBOL
        defaultAktienShouldNotBeFound("symbol.contains=" + UPDATED_SYMBOL);
    }

    @Test
    @Transactional
    public void getAllAktiensBySymbolNotContainsSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where symbol does not contain DEFAULT_SYMBOL
        defaultAktienShouldNotBeFound("symbol.doesNotContain=" + DEFAULT_SYMBOL);

        // Get all the aktienList where symbol does not contain UPDATED_SYMBOL
        defaultAktienShouldBeFound("symbol.doesNotContain=" + UPDATED_SYMBOL);
    }


    @Test
    @Transactional
    public void getAllAktiensByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where date equals to DEFAULT_DATE
        defaultAktienShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the aktienList where date equals to UPDATED_DATE
        defaultAktienShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllAktiensByDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where date not equals to DEFAULT_DATE
        defaultAktienShouldNotBeFound("date.notEquals=" + DEFAULT_DATE);

        // Get all the aktienList where date not equals to UPDATED_DATE
        defaultAktienShouldBeFound("date.notEquals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllAktiensByDateIsInShouldWork() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where date in DEFAULT_DATE or UPDATED_DATE
        defaultAktienShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the aktienList where date equals to UPDATED_DATE
        defaultAktienShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllAktiensByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where date is not null
        defaultAktienShouldBeFound("date.specified=true");

        // Get all the aktienList where date is null
        defaultAktienShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    public void getAllAktiensByOpenIsEqualToSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where open equals to DEFAULT_OPEN
        defaultAktienShouldBeFound("open.equals=" + DEFAULT_OPEN);

        // Get all the aktienList where open equals to UPDATED_OPEN
        defaultAktienShouldNotBeFound("open.equals=" + UPDATED_OPEN);
    }

    @Test
    @Transactional
    public void getAllAktiensByOpenIsNotEqualToSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where open not equals to DEFAULT_OPEN
        defaultAktienShouldNotBeFound("open.notEquals=" + DEFAULT_OPEN);

        // Get all the aktienList where open not equals to UPDATED_OPEN
        defaultAktienShouldBeFound("open.notEquals=" + UPDATED_OPEN);
    }

    @Test
    @Transactional
    public void getAllAktiensByOpenIsInShouldWork() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where open in DEFAULT_OPEN or UPDATED_OPEN
        defaultAktienShouldBeFound("open.in=" + DEFAULT_OPEN + "," + UPDATED_OPEN);

        // Get all the aktienList where open equals to UPDATED_OPEN
        defaultAktienShouldNotBeFound("open.in=" + UPDATED_OPEN);
    }

    @Test
    @Transactional
    public void getAllAktiensByOpenIsNullOrNotNull() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where open is not null
        defaultAktienShouldBeFound("open.specified=true");

        // Get all the aktienList where open is null
        defaultAktienShouldNotBeFound("open.specified=false");
    }

    @Test
    @Transactional
    public void getAllAktiensByOpenIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where open is greater than or equal to DEFAULT_OPEN
        defaultAktienShouldBeFound("open.greaterThanOrEqual=" + DEFAULT_OPEN);

        // Get all the aktienList where open is greater than or equal to UPDATED_OPEN
        defaultAktienShouldNotBeFound("open.greaterThanOrEqual=" + UPDATED_OPEN);
    }

    @Test
    @Transactional
    public void getAllAktiensByOpenIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where open is less than or equal to DEFAULT_OPEN
        defaultAktienShouldBeFound("open.lessThanOrEqual=" + DEFAULT_OPEN);

        // Get all the aktienList where open is less than or equal to SMALLER_OPEN
        defaultAktienShouldNotBeFound("open.lessThanOrEqual=" + SMALLER_OPEN);
    }

    @Test
    @Transactional
    public void getAllAktiensByOpenIsLessThanSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where open is less than DEFAULT_OPEN
        defaultAktienShouldNotBeFound("open.lessThan=" + DEFAULT_OPEN);

        // Get all the aktienList where open is less than UPDATED_OPEN
        defaultAktienShouldBeFound("open.lessThan=" + UPDATED_OPEN);
    }

    @Test
    @Transactional
    public void getAllAktiensByOpenIsGreaterThanSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where open is greater than DEFAULT_OPEN
        defaultAktienShouldNotBeFound("open.greaterThan=" + DEFAULT_OPEN);

        // Get all the aktienList where open is greater than SMALLER_OPEN
        defaultAktienShouldBeFound("open.greaterThan=" + SMALLER_OPEN);
    }


    @Test
    @Transactional
    public void getAllAktiensByCloseIsEqualToSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where close equals to DEFAULT_CLOSE
        defaultAktienShouldBeFound("close.equals=" + DEFAULT_CLOSE);

        // Get all the aktienList where close equals to UPDATED_CLOSE
        defaultAktienShouldNotBeFound("close.equals=" + UPDATED_CLOSE);
    }

    @Test
    @Transactional
    public void getAllAktiensByCloseIsNotEqualToSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where close not equals to DEFAULT_CLOSE
        defaultAktienShouldNotBeFound("close.notEquals=" + DEFAULT_CLOSE);

        // Get all the aktienList where close not equals to UPDATED_CLOSE
        defaultAktienShouldBeFound("close.notEquals=" + UPDATED_CLOSE);
    }

    @Test
    @Transactional
    public void getAllAktiensByCloseIsInShouldWork() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where close in DEFAULT_CLOSE or UPDATED_CLOSE
        defaultAktienShouldBeFound("close.in=" + DEFAULT_CLOSE + "," + UPDATED_CLOSE);

        // Get all the aktienList where close equals to UPDATED_CLOSE
        defaultAktienShouldNotBeFound("close.in=" + UPDATED_CLOSE);
    }

    @Test
    @Transactional
    public void getAllAktiensByCloseIsNullOrNotNull() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where close is not null
        defaultAktienShouldBeFound("close.specified=true");

        // Get all the aktienList where close is null
        defaultAktienShouldNotBeFound("close.specified=false");
    }

    @Test
    @Transactional
    public void getAllAktiensByCloseIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where close is greater than or equal to DEFAULT_CLOSE
        defaultAktienShouldBeFound("close.greaterThanOrEqual=" + DEFAULT_CLOSE);

        // Get all the aktienList where close is greater than or equal to UPDATED_CLOSE
        defaultAktienShouldNotBeFound("close.greaterThanOrEqual=" + UPDATED_CLOSE);
    }

    @Test
    @Transactional
    public void getAllAktiensByCloseIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where close is less than or equal to DEFAULT_CLOSE
        defaultAktienShouldBeFound("close.lessThanOrEqual=" + DEFAULT_CLOSE);

        // Get all the aktienList where close is less than or equal to SMALLER_CLOSE
        defaultAktienShouldNotBeFound("close.lessThanOrEqual=" + SMALLER_CLOSE);
    }

    @Test
    @Transactional
    public void getAllAktiensByCloseIsLessThanSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where close is less than DEFAULT_CLOSE
        defaultAktienShouldNotBeFound("close.lessThan=" + DEFAULT_CLOSE);

        // Get all the aktienList where close is less than UPDATED_CLOSE
        defaultAktienShouldBeFound("close.lessThan=" + UPDATED_CLOSE);
    }

    @Test
    @Transactional
    public void getAllAktiensByCloseIsGreaterThanSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where close is greater than DEFAULT_CLOSE
        defaultAktienShouldNotBeFound("close.greaterThan=" + DEFAULT_CLOSE);

        // Get all the aktienList where close is greater than SMALLER_CLOSE
        defaultAktienShouldBeFound("close.greaterThan=" + SMALLER_CLOSE);
    }


    @Test
    @Transactional
    public void getAllAktiensByHighIsEqualToSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where high equals to DEFAULT_HIGH
        defaultAktienShouldBeFound("high.equals=" + DEFAULT_HIGH);

        // Get all the aktienList where high equals to UPDATED_HIGH
        defaultAktienShouldNotBeFound("high.equals=" + UPDATED_HIGH);
    }

    @Test
    @Transactional
    public void getAllAktiensByHighIsNotEqualToSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where high not equals to DEFAULT_HIGH
        defaultAktienShouldNotBeFound("high.notEquals=" + DEFAULT_HIGH);

        // Get all the aktienList where high not equals to UPDATED_HIGH
        defaultAktienShouldBeFound("high.notEquals=" + UPDATED_HIGH);
    }

    @Test
    @Transactional
    public void getAllAktiensByHighIsInShouldWork() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where high in DEFAULT_HIGH or UPDATED_HIGH
        defaultAktienShouldBeFound("high.in=" + DEFAULT_HIGH + "," + UPDATED_HIGH);

        // Get all the aktienList where high equals to UPDATED_HIGH
        defaultAktienShouldNotBeFound("high.in=" + UPDATED_HIGH);
    }

    @Test
    @Transactional
    public void getAllAktiensByHighIsNullOrNotNull() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where high is not null
        defaultAktienShouldBeFound("high.specified=true");

        // Get all the aktienList where high is null
        defaultAktienShouldNotBeFound("high.specified=false");
    }

    @Test
    @Transactional
    public void getAllAktiensByHighIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where high is greater than or equal to DEFAULT_HIGH
        defaultAktienShouldBeFound("high.greaterThanOrEqual=" + DEFAULT_HIGH);

        // Get all the aktienList where high is greater than or equal to UPDATED_HIGH
        defaultAktienShouldNotBeFound("high.greaterThanOrEqual=" + UPDATED_HIGH);
    }

    @Test
    @Transactional
    public void getAllAktiensByHighIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where high is less than or equal to DEFAULT_HIGH
        defaultAktienShouldBeFound("high.lessThanOrEqual=" + DEFAULT_HIGH);

        // Get all the aktienList where high is less than or equal to SMALLER_HIGH
        defaultAktienShouldNotBeFound("high.lessThanOrEqual=" + SMALLER_HIGH);
    }

    @Test
    @Transactional
    public void getAllAktiensByHighIsLessThanSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where high is less than DEFAULT_HIGH
        defaultAktienShouldNotBeFound("high.lessThan=" + DEFAULT_HIGH);

        // Get all the aktienList where high is less than UPDATED_HIGH
        defaultAktienShouldBeFound("high.lessThan=" + UPDATED_HIGH);
    }

    @Test
    @Transactional
    public void getAllAktiensByHighIsGreaterThanSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where high is greater than DEFAULT_HIGH
        defaultAktienShouldNotBeFound("high.greaterThan=" + DEFAULT_HIGH);

        // Get all the aktienList where high is greater than SMALLER_HIGH
        defaultAktienShouldBeFound("high.greaterThan=" + SMALLER_HIGH);
    }


    @Test
    @Transactional
    public void getAllAktiensByLowIsEqualToSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where low equals to DEFAULT_LOW
        defaultAktienShouldBeFound("low.equals=" + DEFAULT_LOW);

        // Get all the aktienList where low equals to UPDATED_LOW
        defaultAktienShouldNotBeFound("low.equals=" + UPDATED_LOW);
    }

    @Test
    @Transactional
    public void getAllAktiensByLowIsNotEqualToSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where low not equals to DEFAULT_LOW
        defaultAktienShouldNotBeFound("low.notEquals=" + DEFAULT_LOW);

        // Get all the aktienList where low not equals to UPDATED_LOW
        defaultAktienShouldBeFound("low.notEquals=" + UPDATED_LOW);
    }

    @Test
    @Transactional
    public void getAllAktiensByLowIsInShouldWork() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where low in DEFAULT_LOW or UPDATED_LOW
        defaultAktienShouldBeFound("low.in=" + DEFAULT_LOW + "," + UPDATED_LOW);

        // Get all the aktienList where low equals to UPDATED_LOW
        defaultAktienShouldNotBeFound("low.in=" + UPDATED_LOW);
    }

    @Test
    @Transactional
    public void getAllAktiensByLowIsNullOrNotNull() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where low is not null
        defaultAktienShouldBeFound("low.specified=true");

        // Get all the aktienList where low is null
        defaultAktienShouldNotBeFound("low.specified=false");
    }

    @Test
    @Transactional
    public void getAllAktiensByLowIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where low is greater than or equal to DEFAULT_LOW
        defaultAktienShouldBeFound("low.greaterThanOrEqual=" + DEFAULT_LOW);

        // Get all the aktienList where low is greater than or equal to UPDATED_LOW
        defaultAktienShouldNotBeFound("low.greaterThanOrEqual=" + UPDATED_LOW);
    }

    @Test
    @Transactional
    public void getAllAktiensByLowIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where low is less than or equal to DEFAULT_LOW
        defaultAktienShouldBeFound("low.lessThanOrEqual=" + DEFAULT_LOW);

        // Get all the aktienList where low is less than or equal to SMALLER_LOW
        defaultAktienShouldNotBeFound("low.lessThanOrEqual=" + SMALLER_LOW);
    }

    @Test
    @Transactional
    public void getAllAktiensByLowIsLessThanSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where low is less than DEFAULT_LOW
        defaultAktienShouldNotBeFound("low.lessThan=" + DEFAULT_LOW);

        // Get all the aktienList where low is less than UPDATED_LOW
        defaultAktienShouldBeFound("low.lessThan=" + UPDATED_LOW);
    }

    @Test
    @Transactional
    public void getAllAktiensByLowIsGreaterThanSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where low is greater than DEFAULT_LOW
        defaultAktienShouldNotBeFound("low.greaterThan=" + DEFAULT_LOW);

        // Get all the aktienList where low is greater than SMALLER_LOW
        defaultAktienShouldBeFound("low.greaterThan=" + SMALLER_LOW);
    }


    @Test
    @Transactional
    public void getAllAktiensByVolumeIsEqualToSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where volume equals to DEFAULT_VOLUME
        defaultAktienShouldBeFound("volume.equals=" + DEFAULT_VOLUME);

        // Get all the aktienList where volume equals to UPDATED_VOLUME
        defaultAktienShouldNotBeFound("volume.equals=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    public void getAllAktiensByVolumeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where volume not equals to DEFAULT_VOLUME
        defaultAktienShouldNotBeFound("volume.notEquals=" + DEFAULT_VOLUME);

        // Get all the aktienList where volume not equals to UPDATED_VOLUME
        defaultAktienShouldBeFound("volume.notEquals=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    public void getAllAktiensByVolumeIsInShouldWork() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where volume in DEFAULT_VOLUME or UPDATED_VOLUME
        defaultAktienShouldBeFound("volume.in=" + DEFAULT_VOLUME + "," + UPDATED_VOLUME);

        // Get all the aktienList where volume equals to UPDATED_VOLUME
        defaultAktienShouldNotBeFound("volume.in=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    public void getAllAktiensByVolumeIsNullOrNotNull() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where volume is not null
        defaultAktienShouldBeFound("volume.specified=true");

        // Get all the aktienList where volume is null
        defaultAktienShouldNotBeFound("volume.specified=false");
    }

    @Test
    @Transactional
    public void getAllAktiensByVolumeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where volume is greater than or equal to DEFAULT_VOLUME
        defaultAktienShouldBeFound("volume.greaterThanOrEqual=" + DEFAULT_VOLUME);

        // Get all the aktienList where volume is greater than or equal to UPDATED_VOLUME
        defaultAktienShouldNotBeFound("volume.greaterThanOrEqual=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    public void getAllAktiensByVolumeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where volume is less than or equal to DEFAULT_VOLUME
        defaultAktienShouldBeFound("volume.lessThanOrEqual=" + DEFAULT_VOLUME);

        // Get all the aktienList where volume is less than or equal to SMALLER_VOLUME
        defaultAktienShouldNotBeFound("volume.lessThanOrEqual=" + SMALLER_VOLUME);
    }

    @Test
    @Transactional
    public void getAllAktiensByVolumeIsLessThanSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where volume is less than DEFAULT_VOLUME
        defaultAktienShouldNotBeFound("volume.lessThan=" + DEFAULT_VOLUME);

        // Get all the aktienList where volume is less than UPDATED_VOLUME
        defaultAktienShouldBeFound("volume.lessThan=" + UPDATED_VOLUME);
    }

    @Test
    @Transactional
    public void getAllAktiensByVolumeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

        // Get all the aktienList where volume is greater than DEFAULT_VOLUME
        defaultAktienShouldNotBeFound("volume.greaterThan=" + DEFAULT_VOLUME);

        // Get all the aktienList where volume is greater than SMALLER_VOLUME
        defaultAktienShouldBeFound("volume.greaterThan=" + SMALLER_VOLUME);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAktienShouldBeFound(String filter) throws Exception {
        restAktienMockMvc.perform(get("/api/aktiens?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aktien.getId().intValue())))
            .andExpect(jsonPath("$.[*].symbol").value(hasItem(DEFAULT_SYMBOL)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].open").value(hasItem(DEFAULT_OPEN.doubleValue())))
            .andExpect(jsonPath("$.[*].close").value(hasItem(DEFAULT_CLOSE.doubleValue())))
            .andExpect(jsonPath("$.[*].high").value(hasItem(DEFAULT_HIGH.doubleValue())))
            .andExpect(jsonPath("$.[*].low").value(hasItem(DEFAULT_LOW.doubleValue())))
            .andExpect(jsonPath("$.[*].volume").value(hasItem(DEFAULT_VOLUME)));

        // Check, that the count call also returns 1
        restAktienMockMvc.perform(get("/api/aktiens/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAktienShouldNotBeFound(String filter) throws Exception {
        restAktienMockMvc.perform(get("/api/aktiens?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAktienMockMvc.perform(get("/api/aktiens/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingAktien() throws Exception {
        // Get the aktien
        restAktienMockMvc.perform(get("/api/aktiens/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAktien() throws Exception {
        // Initialize the database
        aktienService.save(aktien);

        int databaseSizeBeforeUpdate = aktienRepository.findAll().size();

        // Update the aktien
        Aktien updatedAktien = aktienRepository.findById(aktien.getId()).get();
        // Disconnect from session so that the updates on updatedAktien are not directly saved in db
        em.detach(updatedAktien);
        updatedAktien
            .symbol(UPDATED_SYMBOL)
            .date(UPDATED_DATE)
            .open(UPDATED_OPEN)
            .close(UPDATED_CLOSE)
            .high(UPDATED_HIGH)
            .low(UPDATED_LOW)
            .volume(UPDATED_VOLUME);

        restAktienMockMvc.perform(put("/api/aktiens")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedAktien)))
            .andExpect(status().isOk());

        // Validate the Aktien in the database
        List<Aktien> aktienList = aktienRepository.findAll();
        assertThat(aktienList).hasSize(databaseSizeBeforeUpdate);
        Aktien testAktien = aktienList.get(aktienList.size() - 1);
        assertThat(testAktien.getSymbol()).isEqualTo(UPDATED_SYMBOL);
        assertThat(testAktien.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testAktien.getOpen()).isEqualTo(UPDATED_OPEN);
        assertThat(testAktien.getClose()).isEqualTo(UPDATED_CLOSE);
        assertThat(testAktien.getHigh()).isEqualTo(UPDATED_HIGH);
        assertThat(testAktien.getLow()).isEqualTo(UPDATED_LOW);
        assertThat(testAktien.getVolume()).isEqualTo(UPDATED_VOLUME);
    }

    @Test
    @Transactional
    public void updateNonExistingAktien() throws Exception {
        int databaseSizeBeforeUpdate = aktienRepository.findAll().size();

        // Create the Aktien

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAktienMockMvc.perform(put("/api/aktiens")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(aktien)))
            .andExpect(status().isBadRequest());

        // Validate the Aktien in the database
        List<Aktien> aktienList = aktienRepository.findAll();
        assertThat(aktienList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteAktien() throws Exception {
        // Initialize the database
        aktienService.save(aktien);

        int databaseSizeBeforeDelete = aktienRepository.findAll().size();

        // Delete the aktien
        restAktienMockMvc.perform(delete("/api/aktiens/{id}", aktien.getId())
            .accept(TestUtil.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Aktien> aktienList = aktienRepository.findAll();
        assertThat(aktienList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
