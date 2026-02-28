package pfe.cb_management.dto;

import lombok.*;
import pfe.cb_management.enums.StatutRendezVous;
import pfe.cb_management.enums.TypeClient;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RendezVousResponse {

    private Long id;

    // Client
    private String nomClient;
    private String prenomClient;
    private String telephoneClient;
    private TypeClient typeClient;

    // Planification
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private Integer nbHeures;

    // Statut
    private StatutRendezVous statut;

    // Réceptionniste
    private Long createdById;
    private String createdByNom;
    private String createdByPrenom;

    // Services
    private List<ServiceRendezVousDto> services;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
