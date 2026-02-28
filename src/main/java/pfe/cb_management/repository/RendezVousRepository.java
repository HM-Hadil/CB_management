package pfe.cb_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pfe.cb_management.entity.RendezVous;
import pfe.cb_management.enums.StatutRendezVous;

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
}
