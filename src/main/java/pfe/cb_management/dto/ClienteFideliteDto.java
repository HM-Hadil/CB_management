package pfe.cb_management.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClienteFideliteDto {

    private String nomClient;
    private String prenomClient;
    private String telephoneClient;

    /** Nombre de services ce mois-ci dans les RDV terminés */
    private int totalServices;

    /** Offres gagnées ce mois-ci = totalServices / 5 */
    private int offresGagnees;

    /** Offres déjà utilisées ce mois-ci */
    private int offresUtilisees;

    /** Offres encore disponibles ce mois-ci = offresGagnees - offresUtilisees */
    private int offresDisponibles;

    /** Progression vers la prochaine offre = totalServices % 5 (valeur 0-4) */
    private int servicesVersProchainOffre;

    /** Date du premier rendez-vous terminé de cette cliente */
    private LocalDate clientDepuis;

    /** Mois de référence au format "yyyy-MM", ex : "2026-03" */
    private String moisAnnee;

    /** Détail des offres utilisées ce mois-ci (type + date) */
    private List<OffreUtiliseeDto> offresUtiliseesDetails;
}
