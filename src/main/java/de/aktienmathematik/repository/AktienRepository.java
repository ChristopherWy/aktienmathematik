package de.aktienmathematik.repository;

import de.aktienmathematik.domain.Aktien;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Aktien entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AktienRepository extends JpaRepository<Aktien, Long>, JpaSpecificationExecutor<Aktien> {

}
