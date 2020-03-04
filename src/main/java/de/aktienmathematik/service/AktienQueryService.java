package de.aktienmathematik.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import de.aktienmathematik.domain.Aktien;
import de.aktienmathematik.domain.*; // for static metamodels
import de.aktienmathematik.repository.AktienRepository;
import de.aktienmathematik.service.dto.AktienCriteria;

/**
 * Service for executing complex queries for {@link Aktien} entities in the database.
 * The main input is a {@link AktienCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Aktien} or a {@link Page} of {@link Aktien} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AktienQueryService extends QueryService<Aktien> {

    private final Logger log = LoggerFactory.getLogger(AktienQueryService.class);

    private final AktienRepository aktienRepository;

    public AktienQueryService(AktienRepository aktienRepository) {
        this.aktienRepository = aktienRepository;
    }

    /**
     * Return a {@link List} of {@link Aktien} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Aktien> findByCriteria(AktienCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Aktien> specification = createSpecification(criteria);
        return aktienRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Aktien} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Aktien> findByCriteria(AktienCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Aktien> specification = createSpecification(criteria);
        return aktienRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AktienCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Aktien> specification = createSpecification(criteria);
        return aktienRepository.count(specification);
    }

    /**
     * Function to convert {@link AktienCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Aktien> createSpecification(AktienCriteria criteria) {
        Specification<Aktien> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Aktien_.id));
            }
            if (criteria.getSymbol() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSymbol(), Aktien_.symbol));
            }
            if (criteria.getDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDate(), Aktien_.date));
            }
            if (criteria.getOpen() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getOpen(), Aktien_.open));
            }
            if (criteria.getClose() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getClose(), Aktien_.close));
            }
            if (criteria.getHigh() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getHigh(), Aktien_.high));
            }
            if (criteria.getLow() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLow(), Aktien_.low));
            }
            if (criteria.getVolume() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getVolume(), Aktien_.volume));
            }
        }
        return specification;
    }
}
