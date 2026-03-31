package pfe.cb_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pfe.cb_management.entity.OffreFidelite;

import java.util.List;

public interface OffreFideliteRepository extends JpaRepository<OffreFidelite, Long> {

    /** Nombre d'offres déjà utilisées ce mois-ci pour une cliente */
    int countByTelephoneClientAndMoisAnnee(String telephoneClient, String moisAnnee);

    /** Détail des offres utilisées ce mois-ci pour une cliente (triées par date) */
    List<OffreFidelite> findByTelephoneClientAndMoisAnneeOrderByCreatedAtAsc(String telephoneClient, String moisAnnee);
}
