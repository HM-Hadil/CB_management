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
}
