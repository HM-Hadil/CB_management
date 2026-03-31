package pfe.cb_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pfe.cb_management.entity.AvisCliente;

import java.util.List;
import java.util.Optional;

public interface AvisClienteRepository extends JpaRepository<AvisCliente, Long> {

    List<AvisCliente> findAllByOrderByCreatedAtDesc();

    boolean existsByRendezVousId(Long rendezVousId);

    Optional<AvisCliente> findByRendezVousId(Long rendezVousId);
}
