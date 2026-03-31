package pfe.cb_management.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AvisClienteDto {

    private Long id;
    private Long rendezVousId;

    private String nomClient;
    private String prenomClient;
    private String telephoneClient;

    /** Note de 1 à 5 étoiles */
    private Integer note;

    /** Commentaire optionnel */
    private String commentaire;

    private LocalDateTime createdAt;
}
