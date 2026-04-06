package pfe.cb_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pfe.cb_management.entity.User;
import pfe.cb_management.enums.Role;
import pfe.cb_management.enums.Specialite;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByRoleNot(Role role);

    // ── Employés disponibles pour une spécialité et un créneau ──
    // Un employé est occupé si son RDV est EN_ATTENTE, CONFIRME ou EN_COURS.
    // Seuls ANNULE et TERMINE libèrent l'employé.
    @Query("""
            SELECT u FROM User u
            WHERE :specialite MEMBER OF u.specialites
              AND u.role = 'EMPLOYEE'
              AND u.activated = true
              AND u.id NOT IN (
                  SELECT srv.employee.id FROM ServiceRendezVous srv
                  WHERE srv.rendezVous.statut NOT IN ('ANNULE', 'TERMINE')
                    AND srv.rendezVous.dateDebut < :dateFin
                    AND srv.rendezVous.dateFin  > :dateDebut
              )
            """)
    List<User> findAvailableEmployees(
            @Param("specialite") Specialite specialite,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin
    );
}
