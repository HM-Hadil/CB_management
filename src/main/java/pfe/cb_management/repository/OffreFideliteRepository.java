package pfe.cb_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pfe.cb_management.entity.OffreFidelite;

import java.util.Optional;

public interface OffreFideliteRepository extends JpaRepository<OffreFidelite, Long> {

    Optional<OffreFidelite> findByTelephoneClient(String telephoneClient);
}
