package pfe.cb_management.dto;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClienteFideliteDto {

    private String nomClient;
    private String prenomClient;
    private String telephoneClient;

    /** Nombre total de services dans les RDV terminés */
    private int totalServices;

    /** Offres gagnées = totalServices / 5 */
    private int offresGagnees;

    /** Offres déjà utilisées (depuis la table offre_fidelite) */
    private int offresUtilisees;

    /** Offres encore disponibles = offresGagnees - offresUtilisees */
    private int offresDisponibles;

    /** Progression vers la prochaine offre = totalServices % 5 (valeur 0-4) */
    private int servicesVersProchainOffre;

    /** Date du premier rendez-vous terminé de cette cliente */
    private LocalDate clientDepuis;
}
