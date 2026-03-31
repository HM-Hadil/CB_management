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
import java.util.Map;
import java.util.LinkedHashMap;

@Service
@RequiredArgsConstructor
public class FideliteService {

    private final RendezVousRepository rendezVousRepository;
    private final OffreFideliteRepository offreFideliteRepository;

    private static final int SERVICES_PAR_OFFRE = 5;
    private static final DateTimeFormatter MOIS_ANNEE_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * Retourne la liste de toutes les clientes ayant au moins un service terminé CE MOIS-CI ou LE MOIS DERNIER,
     * avec leurs statistiques de fidélité calculées sur le mois courant.
     */
    public List<ClienteFideliteDto> getAllClientesFidelite() {
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();
        String moisAnneeActuel = now.format(MOIS_ANNEE_FMT);

        try {
            // Récupérer les clientes du mois courant
            List<Object[]> thisMonthRows = rendezVousRepository.countServicesByClientForMonth(currentMonth, currentYear);

            // Calculer le mois précédent
            LocalDate prevMonth = now.minusMonths(1);
            int prevMonthValue = prevMonth.getMonthValue();
            int prevYear = prevMonth.getYear();

            // Récupérer les clientes du mois dernier
            List<Object[]> prevMonthRows = rendezVousRepository.countServicesByClientForMonth(prevMonthValue, prevYear);

            // Fusionner: garder les uniques par téléphone (index 2), prioriser ce mois-ci
            Map<String, Object[]> mergedMap = new LinkedHashMap<>();

            // Ajouter mois dernier d'abord
            for (Object[] row : prevMonthRows) {
                if (row != null && row.length >= 3) {
                    String telephone = (String) row[2];
                    mergedMap.put(telephone, row);
                }
            }

            // Puis ce mois-ci (écrase si existe)
            for (Object[] row : thisMonthRows) {
                if (row != null && row.length >= 3) {
                    String telephone = (String) row[2];
                    mergedMap.put(telephone, row);
                }
            }

            // Construire les DTOs
            return mergedMap.values().stream()
                    .map(row -> buildDto(row, moisAnneeActuel))
                    .sorted(Comparator.comparing(ClienteFideliteDto::getClientDepuis).reversed())
                    .toList();

        } catch (Exception e) {
            // Log et retourner liste vide plutôt que 500 error
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
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();
        String moisAnnee = now.format(MOIS_ANNEE_FMT);

        // Vérifier que la cliente existe et a une offre disponible ce mois-ci
        List<Object[]> rows = rendezVousRepository.countServicesByClientForMonth(month, year);
        Object[] clientRow = rows.stream()
                .filter(r -> telephoneClient.equals(r[2]))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Aucune cliente trouvée avec ce numéro"));

        ClienteFideliteDto cliente = buildDto(clientRow, moisAnnee);

        if (cliente.getOffresDisponibles() <= 0) {
            throw new IllegalStateException("Aucune offre disponible pour cette cliente ce mois-ci");
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

        // Retourner le DTO mis à jour via buildDto (relit les utilisations depuis la BDD)
        Object[] updatedRow = rows.stream()
                .filter(r -> telephoneClient.equals(r[2]))
                .findFirst()
                .orElseThrow();
        return buildDto(updatedRow, moisAnnee);
    }

    // ── private helper ─────────────────────────────────────────────
    private ClienteFideliteDto buildDto(Object[] row, String moisAnnee) {
        String nom       = (String) row[0];
        String prenom    = (String) row[1];
        String telephone = (String) row[2];
        int totalServices = ((Number) row[3]).intValue();
        LocalDateTime premierRdv = (LocalDateTime) row[4];

        int offresGagnees  = totalServices / SERVICES_PAR_OFFRE;
        int progression    = totalServices % SERVICES_PAR_OFFRE;

        List<OffreFidelite> utilisations = offreFideliteRepository
                .findByTelephoneClientAndMoisAnneeOrderByCreatedAtAsc(telephone, moisAnnee);

        List<OffreUtiliseeDto> details = utilisations.stream()
                .map(o -> OffreUtiliseeDto.builder()
                        .typeOffre(o.getTypeOffre())
                        .dateUtilisation(o.getCreatedAt())
                        .build())
                .toList();

        int offresUtilisees    = utilisations.size();
        int offresDisponibles  = Math.max(0, offresGagnees - offresUtilisees);

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
                .moisAnnee(moisAnnee)
                .offresUtiliseesDetails(details)
                .build();
    }
}
