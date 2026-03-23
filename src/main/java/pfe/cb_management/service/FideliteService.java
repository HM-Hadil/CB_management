package pfe.cb_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pfe.cb_management.dto.ClienteFideliteDto;
import pfe.cb_management.entity.OffreFidelite;
import pfe.cb_management.repository.OffreFideliteRepository;
import pfe.cb_management.repository.RendezVousRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FideliteService {

    private final RendezVousRepository rendezVousRepository;
    private final OffreFideliteRepository offreFideliteRepository;

    private static final int SERVICES_PAR_OFFRE = 5;

    /**
     * Retourne la liste de toutes les clientes ayant au moins un service terminé,
     * avec leurs statistiques de fidélité.
     */
    public List<ClienteFideliteDto> getAllClientesFidelite() {
        List<Object[]> rows = rendezVousRepository.countServicesByClient();

        return rows.stream()
                .map(row -> {
                    String nom       = (String) row[0];
                    String prenom    = (String) row[1];
                    String telephone = (String) row[2];
                    int totalServices = ((Number) row[3]).intValue();
                    LocalDateTime premierRdv = (LocalDateTime) row[4];

                    int offresGagnees  = totalServices / SERVICES_PAR_OFFRE;
                    int progression    = totalServices % SERVICES_PAR_OFFRE;

                    int offresUtilisees = offreFideliteRepository
                            .findByTelephoneClient(telephone)
                            .map(OffreFidelite::getOffresUtilisees)
                            .orElse(0);

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
                            .build();
                })
                .sorted(Comparator.comparing(ClienteFideliteDto::getClientDepuis).reversed())
                .toList();
    }

    /**
     * Marque une offre comme utilisée pour la cliente identifiée par son téléphone.
     */
    @Transactional
    public ClienteFideliteDto utiliserOffre(String telephoneClient) {
        // Calculer d'abord les stats pour vérifier qu'une offre est disponible
        List<ClienteFideliteDto> toutes = getAllClientesFidelite();

        ClienteFideliteDto cliente = toutes.stream()
                .filter(c -> telephoneClient.equals(c.getTelephoneClient()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Aucune cliente trouvée avec ce numéro"));

        if (cliente.getOffresDisponibles() <= 0) {
            throw new IllegalStateException("Aucune offre disponible pour cette cliente");
        }

        // Trouver ou créer l'enregistrement OffreFidelite
        OffreFidelite offre = offreFideliteRepository
                .findByTelephoneClient(telephoneClient)
                .orElseGet(() -> OffreFidelite.builder()
                        .nomClient(cliente.getNomClient())
                        .prenomClient(cliente.getPrenomClient())
                        .telephoneClient(telephoneClient)
                        .offresUtilisees(0)
                        .build());

        offre.setOffresUtilisees(offre.getOffresUtilisees() + 1);
        offreFideliteRepository.save(offre);

        // Retourner le DTO mis à jour
        return ClienteFideliteDto.builder()
                .nomClient(cliente.getNomClient())
                .prenomClient(cliente.getPrenomClient())
                .telephoneClient(telephoneClient)
                .totalServices(cliente.getTotalServices())
                .offresGagnees(cliente.getOffresGagnees())
                .offresUtilisees(offre.getOffresUtilisees())
                .offresDisponibles(cliente.getOffresGagnees() - offre.getOffresUtilisees())
                .servicesVersProchainOffre(cliente.getServicesVersProchainOffre())
                .clientDepuis(cliente.getClientDepuis())
                .build();
    }
}
