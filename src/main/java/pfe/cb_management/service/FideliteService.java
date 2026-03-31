package pfe.cb_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pfe.cb_management.dto.ClienteFideliteDto;
import pfe.cb_management.dto.OffreUtiliseeDto;
import pfe.cb_management.entity.OffreFidelite;
import pfe.cb_management.enums.TypeOffre;
import pfe.cb_management.repository.OffreFideliteRepository;
import pfe.cb_management.repository.RendezVousRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FideliteService {

    private final RendezVousRepository rendezVousRepository;
    private final OffreFideliteRepository offreFideliteRepository;

    private static final int SERVICES_PAR_OFFRE = 5;
    private static final DateTimeFormatter MOIS_ANNEE_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * Retourne la liste de toutes les clientes ayant au moins un service terminé (tous temps),
     * avec leurs statistiques de fidélité cumulatives.
     */
    public List<ClienteFideliteDto> getAllClientesFidelite() {
        try {
            LocalDate today = LocalDate.now();
            List<Object[]> rows = rendezVousRepository.countServicesByClientForMonth(today.getMonthValue(), today.getYear());

            if (rows.isEmpty()) {
                rows = rendezVousRepository.countServicesByClientAllTime();
            }

            return rows.stream()
                    .filter(row -> row != null && row.length >= 5)
                    .filter(row -> row[2] != null) // numéro de téléphone requis pour affichage
                    .map(this::buildDto)
                    .sorted(Comparator.comparing(ClienteFideliteDto::getClientDepuis).reversed())
                    .toList();

        } catch (Exception e) {
            System.err.println("Erreur dans getAllClientesFidelite: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Utilise une offre pour la cliente identifiée par son téléphone.
     * Sauvegarde le type d'offre choisi (service gratuit ou promo prochain service).
     */
    @Transactional
    public ClienteFideliteDto utiliserOffre(String telephoneClient, TypeOffre typeOffre) {
        String moisAnnee = LocalDate.now().format(MOIS_ANNEE_FMT);

        // Vérifier que la cliente existe et a une offre disponible
        List<Object[]> rows = rendezVousRepository.countServicesByClientAllTime();
        Object[] clientRow = rows.stream()
                .filter(r -> telephoneClient.equals(r[2]))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Aucune cliente trouvée avec ce numéro"));

        ClienteFideliteDto cliente = buildDto(clientRow);

        if (cliente.getOffresDisponibles() <= 0) {
            throw new IllegalStateException("Aucune offre disponible pour cette cliente");
        }

        // Enregistrer l'utilisation de l'offre
        OffreFidelite offre = OffreFidelite.builder()
                .nomClient(cliente.getNomClient())
                .prenomClient(cliente.getPrenomClient())
                .telephoneClient(telephoneClient)
                .typeOffre(typeOffre)
                .moisAnnee(moisAnnee)
                .build();
        offreFideliteRepository.save(offre);

        // Retourner le DTO mis à jour (relit les utilisations depuis la BDD après save)
        List<Object[]> updatedRows = rendezVousRepository.countServicesByClientForMonth(LocalDate.now().getMonthValue(), LocalDate.now().getYear());
        return updatedRows.stream()
                .filter(r -> r != null && r.length >= 5)
                .filter(r -> telephoneClient.equals(r[2]))
                .findFirst()
                .map(this::buildDto)
                .orElse(buildDto(clientRow));
    }

    // ── private helper ─────────────────────────────────────────────
    private ClienteFideliteDto buildDto(Object[] row) {
        String nom        = (String) row[0];
        String prenom     = (String) row[1];
        String telephone  = (String) row[2];
        int totalServices = ((Number) row[3]).intValue();
        LocalDateTime premierRdv = (LocalDateTime) row[4];

        int offresGagnees = totalServices / SERVICES_PAR_OFFRE;
        int progression   = totalServices % SERVICES_PAR_OFFRE;

        List<OffreFidelite> utilisations = offreFideliteRepository
                .findByTelephoneClientOrderByCreatedAtAsc(telephone);

        List<OffreUtiliseeDto> details = utilisations.stream()
                .map(o -> OffreUtiliseeDto.builder()
                        .typeOffre(o.getTypeOffre())
                        .dateUtilisation(o.getCreatedAt())
                        .build())
                .toList();

        int offresUtilisees   = utilisations.size();
        int offresDisponibles = Math.max(0, offresGagnees - offresUtilisees);

        return ClienteFideliteDto.builder()
                .nomClient(nom)
                .prenomClient(prenom)
                .telephoneClient(telephone)
                .totalServices(totalServices)
                .offresGagnees(offresGagnees)
                .offresUtilisees(offresUtilisees)
                .offresDisponibles(offresDisponibles)
                .servicesVersProchainOffre(progression)
                .clientDepuis(premierRdv != null ? premierRdv.toLocalDate() : LocalDate.now())
                .moisAnnee(LocalDate.now().format(MOIS_ANNEE_FMT))
                .offresUtiliseesDetails(details)
                .build();
    }
}
