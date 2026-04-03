package pfe.cb_management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pfe.cb_management.entity.RendezVous;
import pfe.cb_management.enums.StatutRendezVous;
import pfe.cb_management.repository.RendezVousRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RendezVousScheduler {

    private final RendezVousRepository rendezVousRepository;

    /**
     * Toutes les minutes, vérifie si des rendez-vous ont dépassé leur dateFin
     * et les marque automatiquement comme TERMINE.
     */
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void marquerRendezVousTermines() {
        List<RendezVous> expires = rendezVousRepository.findByDateFinBeforeAndStatutNotIn(
                LocalDateTime.now(),
                List.of(StatutRendezVous.TERMINE, StatutRendezVous.ANNULE)
        );

        if (!expires.isEmpty()) {
            expires.forEach(rdv -> rdv.setStatut(StatutRendezVous.TERMINE));
            rendezVousRepository.saveAll(expires);
            log.info("[Scheduler] {} rendez-vous marqué(s) TERMINE automatiquement.", expires.size());
        }
    }
}
