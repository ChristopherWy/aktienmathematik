package de.aktienmathematik.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.aktienmathematik.domain.Aktien;
import de.aktienmathematik.domain.Symbol;
import de.aktienmathematik.repository.AktienRepository;
import de.aktienmathematik.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * REST controller for managing {@link de.aktienmathematik.domain.Aktien}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AktienResource {

    private final Logger log = LoggerFactory.getLogger(AktienResource.class);

    private static final String ENTITY_NAME = "aktien";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AktienRepository aktienRepository;

    public AktienResource(AktienRepository aktienRepository) {
        this.aktienRepository = aktienRepository;
    }

    @GetMapping("symbols")
    public ResponseEntity<List<Symbol>> getSymbols() throws URISyntaxException, IOException {
        ArrayList<Symbol> symbolList = new ArrayList<Symbol>();
        log.debug("REST request to get all Symbols");
        String hostname = "10.241.50.94"; //""blntcpfsc02.qee.blntc";
        int port = 8080;
        Proxy proxy = new Proxy(Proxy.Type.HTTP,
            new InetSocketAddress(hostname, port));

        OkHttpClient client = new OkHttpClient.Builder()
            .proxy(proxy)
            .build();

        Request request = new Request.Builder()
            .url("https://sandbox.iexapis.com/beta/ref-data/symbols?token=Tpk_bfef6dd096704fc3a02137bc685b5058")
            .get()
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            ObjectMapper mapper = new ObjectMapper();
            LinkedList<HashMap<String,String>> symbols = mapper.readValue(response.body().string(), new LinkedList<HashMap<String,String>>().getClass());

            for(HashMap<String,String> symbol : symbols) {
                Symbol symbolDto = new Symbol();
                symbolDto.setSymbol(symbol.get("symbol"));
                symbolDto.setFullName(symbol.get("name"));
                symbolList.add(symbolDto);
            }
        }
        return ResponseEntity.created(new URI("/api/symbols/"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, "symbols"))
            .body(symbolList);
    }

    /**
     * {@code POST  /aktiens} : Create a new aktien.
     *
     * @param aktien the aktien to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aktien, or with status {@code 400 (Bad Request)} if the aktien has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/aktiens")
    public ResponseEntity<Aktien> createAktien(@RequestBody Aktien aktien) throws URISyntaxException, IOException {
        log.debug("REST request to save Aktien : {}", aktien);
        if (aktien.getId() != null) {
            throw new BadRequestAlertException("A new aktien cannot already have an ID", ENTITY_NAME, "idexists");
        }
        //Aktien result = aktienRepository.save(aktien);
        Aktien result = loadAktien();
        return ResponseEntity.created(new URI("/api/aktiens/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    private Aktien loadAktien() throws IOException {
        Aktien aktieDto = null;
        String hostname = "10.241.50.94"; //""blntcpfsc02.qee.blntc";
        int port = 8080;
        Proxy proxy = new Proxy(Proxy.Type.HTTP,
            new InetSocketAddress(hostname, port));

        OkHttpClient client = new OkHttpClient.Builder()
            .proxy(proxy)
            .build();

        // https://cloud.iexapis.com/beta/ref-data/symbols?token=
        // https://cloud.iexapis.com/stable/stock/FB/chart/date/20200203?token=
        // https://cloud.iexapis.com/stable/stock/BAC/intraday-prices?token=&chartLast=20
        // https://cloud.iexapis.com/stable/stock/aapl/chart?token=
        // https://cloud.iexapis.com/stable/stock/twtr/chart/5y?token=

        // https://sandbox.iexapis.com/stable/stock/twtr/chart/5y?token=Tpk_bfef6dd096704fc3a02137bc685b5058
        Request request = new Request.Builder()
            .url("https://sandbox.iexapis.com/stable/stock/aapl/chart/1y?token=Tpk_bfef6dd096704fc3a02137bc685b5058")
            .get()
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            ObjectMapper mapper = new ObjectMapper();
            LinkedList<HashMap<String,String>> aktien = mapper.readValue(response.body().string(), new LinkedList<HashMap<String,String>>().getClass());

            for(HashMap<String,String> aktie : aktien) {
                aktieDto = new Aktien();
                aktieDto.setSymbol("Apple Inc. (aapl)");
                aktieDto.setClose(Double.valueOf(String.valueOf(aktie.get("close"))));
                LocalDate date = LocalDate.parse(String.valueOf(aktie.get("date")));
                Instant instant = date.atStartOfDay(ZoneId.of("Europe/Paris")).toInstant();
                aktieDto.setDate(instant);
                aktieDto.setHigh(Double.valueOf(String.valueOf(aktie.get("high"))));
                aktieDto.setLow(Double.valueOf(String.valueOf(aktie.get("low"))));
                aktieDto.setOpen(Double.valueOf(String.valueOf(aktie.get("open"))));
                aktieDto.setVolume(Integer.valueOf(String.valueOf(aktie.get("volume"))));
                aktienRepository.save(aktieDto);
            }
        }
        return aktieDto;
    }

    /**
     * {@code PUT  /aktiens} : Updates an existing aktien.
     *
     * @param aktien the aktien to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aktien,
     * or with status {@code 400 (Bad Request)} if the aktien is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aktien couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/aktiens")
    public ResponseEntity<Aktien> updateAktien(@RequestBody Aktien aktien) throws URISyntaxException {
        log.debug("REST request to update Aktien : {}", aktien);
        if (aktien.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Aktien result = aktienRepository.save(aktien);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aktien.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /aktiens} : get all the aktiens.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of aktiens in body.
     */
    @GetMapping("/aktiens")
    public ResponseEntity<List<Aktien>> getAllAktiens(Pageable pageable) {
        log.debug("REST request to get a page of Aktiens");
        Page<Aktien> page = aktienRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /aktiens/:id} : get the "id" aktien.
     *
     * @param id the id of the aktien to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aktien, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/aktiens/{id}")
    public ResponseEntity<Aktien> getAktien(@PathVariable Long id) {
        log.debug("REST request to get Aktien : {}", id);
        Optional<Aktien> aktien = aktienRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(aktien);
    }

    /**
     * {@code DELETE  /aktiens/:id} : delete the "id" aktien.
     *
     * @param id the id of the aktien to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/aktiens/{id}")
    public ResponseEntity<Void> deleteAktien(@PathVariable Long id) {
        log.debug("REST request to delete Aktien : {}", id);
        aktienRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
