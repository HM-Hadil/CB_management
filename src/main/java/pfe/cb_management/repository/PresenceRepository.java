package pfe.cb_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pfe.cb_management.entity.Presence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PresenceRepository extends JpaRepository<Presence, Long> {

    List<Presence> findByDate(LocalDate date);

    Optional<Presence> findByEmployeeIdAndDate(Long employeeId, LocalDate date);

    List<Presence> findByDateBetween(LocalDate start, LocalDate end);

    @org.springframework.data.jpa.repository.Query("""
            SELECT p.employee.id, p.employee.nom, p.employee.prenom, COUNT(p.id)
            FROM Presence p
            WHERE MONTH(p.date) = :mois
              AND YEAR(p.date) = :annee
              AND p.statut IN ('PRESENT', 'RETARD', 'TERMINE')
            GROUP BY p.employee.id, p.employee.nom, p.employee.prenom
            ORDER BY p.employee.nom
            """)
    List<Object[]> countPresenceParEmployeeParMois(
            @org.springframework.data.repository.query.Param("mois") int mois,
            @org.springframework.data.repository.query.Param("annee") int annee);
}
