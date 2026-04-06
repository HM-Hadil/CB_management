package pfe.cb_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pfe.cb_management.entity.RendezVous;
import pfe.cb_management.entity.ServiceRendezVous;
import pfe.cb_management.enums.StatutRendezVous;

import java.time.LocalDateTime;
import java.util.List;

public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {

    List<RendezVous> findAllByOrderByDateDebutDesc();

    List<RendezVous> findByStatut(StatutRendezVous statut);

    List<RendezVous> findByCreatedById(Long receptionnisteId);

    // Tous les rendez-vous où l'employé est assigné à au moins un service
    @Query("""
            SELECT DISTINCT r FROM RendezVous r
            JOIN r.services s
            WHERE s.employee.id = :employeeId
            ORDER BY r.dateDebut DESC
            """)
    List<RendezVous> findByEmployeeId(@Param("employeeId") Long employeeId);

    // Rendez-vous d'un employé filtrés par statut
    @Query("""
            SELECT DISTINCT r FROM RendezVous r
            JOIN r.services s
            WHERE s.employee.id = :employeeId
              AND r.statut = :statut
            ORDER BY r.dateDebut DESC
            """)
    List<RendezVous> findByEmployeeIdAndStatut(
            @Param("employeeId") Long employeeId,
            @Param("statut") StatutRendezVous statut
    );

    // Rendez-vous en conflit horaire pour un employé donné (hors rendez-vous optionnel à exclure)
    @Query("""
            SELECT DISTINCT r FROM RendezVous r
            JOIN r.services s
            WHERE s.employee.id = :employeeId
              AND r.statut <> 'ANNULE'
              AND r.dateDebut < :dateFin
              AND r.dateFin > :dateDebut
              AND (:excludeRdvId IS NULL OR r.id <> :excludeRdvId)
            """)
    List<RendezVous> findConflictingRendezVousForEmployee(
            @Param("employeeId") Long employeeId,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin,
            @Param("excludeRdvId") Long excludeRdvId
    );

    @Query("""
            SELECT srv FROM ServiceRendezVous srv
            JOIN srv.rendezVous r
            WHERE srv.employee.id = :employeeId
              AND r.statut <> 'ANNULE'
              AND (:excludeRdvId IS NULL OR r.id <> :excludeRdvId)
            """)
    List<ServiceRendezVous> findServicesForEmployee(
            @Param("employeeId") Long employeeId,
            @Param("excludeRdvId") Long excludeRdvId
    );

    // Nombre de services par cliente pour un mois/année donné (RDV terminés uniquement)
    @Query("""
            SELECT rv.nomClient, rv.prenomClient, rv.telephoneClient, COUNT(srv.id), MIN(rv.dateDebut)
            FROM RendezVous rv
            JOIN rv.services srv
            WHERE rv.statut = 'TERMINE'
              AND MONTH(rv.dateDebut) = :month
              AND YEAR(rv.dateDebut) = :year
            GROUP BY rv.nomClient, rv.prenomClient, rv.telephoneClient
            """)
    List<Object[]> countServicesByClientForMonth(@Param("month") int month, @Param("year") int year);

    // Statistiques RDV par employé par mois pour une année donnée
    @Query("""
            SELECT s.employee.id, s.employee.nom, s.employee.prenom,
                   MONTH(r.dateDebut), COUNT(DISTINCT r.id)
            FROM ServiceRendezVous s
            JOIN s.rendezVous r
            WHERE YEAR(r.dateDebut) = :annee
              AND r.statut <> 'ANNULE'
              AND s.employee IS NOT NULL
            GROUP BY s.employee.id, s.employee.nom, s.employee.prenom, MONTH(r.dateDebut)
            ORDER BY s.employee.nom, MONTH(r.dateDebut)
            """)
    List<Object[]> countRdvParEmployeeParMois(@Param("annee") int annee);

    // Nombre de services total par cliente tous temps confondus (RDV terminés uniquement)
    @Query("""
            SELECT rv.nomClient, rv.prenomClient, rv.telephoneClient, COUNT(srv.id), MIN(rv.dateDebut)
            FROM RendezVous rv
            JOIN rv.services srv
            WHERE rv.statut = 'TERMINE'
              AND rv.telephoneClient IS NOT NULL
            GROUP BY rv.nomClient, rv.prenomClient, rv.telephoneClient
            """)
    List<Object[]> countServicesByClientAllTime();
}
