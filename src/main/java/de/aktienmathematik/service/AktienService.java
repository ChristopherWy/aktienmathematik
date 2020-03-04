package de.aktienmathematik.service;

import de.aktienmathematik.domain.Aktien;
import de.aktienmathematik.repository.AktienRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Aktien}.
 */
@Service
@Transactional
public class AktienService {

    private final Logger log = LoggerFactory.getLogger(AktienService.class);

    private final AktienRepository aktienRepository;

    public AktienService(AktienRepository aktienRepository) {
        this.aktienRepository = aktienRepository;
    }

    /**
     * Save a aktien.
     *
     * @param aktien the entity to save.
     * @return the persisted entity.
     */
    public Aktien save(Aktien aktien) {
        log.debug("Request to save Aktien : {}", aktien);
        return aktienRepository.save(aktien);
    }

    /**
     * Get all the aktiens.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Aktien> findAll(Pageable pageable) {
        log.debug("Request to get all Aktiens");
        return aktienRepository.findAll(pageable);
    }

    /**
     * Get one aktien by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Aktien> findOne(Long id) {
        log.debug("Request to get Aktien : {}", id);
        return aktienRepository.findById(id);
    }

    /**
     * Delete the aktien by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Aktien : {}", id);
        aktienRepository.deleteById(id);
    }
}
