package pfe.cb_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pfe.cb_management.entity.ServiceRendezVous;

import java.util.Optional;

public interface ServiceRendezVousRepository extends JpaRepository<ServiceRendezVous, Long> {

    Optional<ServiceRendezVous> findById(Long id);
}