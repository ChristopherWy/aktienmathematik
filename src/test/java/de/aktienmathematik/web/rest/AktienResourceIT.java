package de.aktienmathematik.web.rest;

import de.aktienmathematik.AktienmathematikApp;
import de.aktienmathematik.domain.Aktien;
import de.aktienmathematik.repository.AktienRepository;
import de.aktienmathematik.web.rest.errors.ExceptionTranslator;

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

    private static final Double DEFAULT_CLOSE = 1D;
    private static final Double UPDATED_CLOSE = 2D;

    private static final Double DEFAULT_HIGH = 1D;
    private static final Double UPDATED_HIGH = 2D;

    private static final Double DEFAULT_LOW = 1D;
    private static final Double UPDATED_LOW = 2D;

    private static final Integer DEFAULT_VOLUME = 1;
    private static final Integer UPDATED_VOLUME = 2;

    @Autowired
    private AktienRepository aktienRepository;

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
        final AktienResource aktienResource = new AktienResource(aktienRepository);
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
    public void getNonExistingAktien() throws Exception {
        // Get the aktien
        restAktienMockMvc.perform(get("/api/aktiens/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAktien() throws Exception {
        // Initialize the database
        aktienRepository.saveAndFlush(aktien);

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
        aktienRepository.saveAndFlush(aktien);

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
