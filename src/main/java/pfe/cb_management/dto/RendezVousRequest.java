package pfe.cb_management.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import pfe.cb_management.enums.TypeClient;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RendezVousRequest {

    @NotBlank(message = "Le nom du client est obligatoire")
    private String nomClient;

    @NotBlank(message = "Le prénom du client est obligatoire")
    private String prenomClient;

    // Téléphone non obligatoire
    private String telephoneClient;

    @NotNull(message = "Le type de client est obligatoire")
    private TypeClient typeClient;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDateTime dateDebut;

    @NotNull(message = "La durée en heures est obligatoire")
    @Positive(message = "La durée doit être supérieure à 0")
    private Integer nbHeures;

    @NotEmpty(message = "Le rendez-vous doit contenir au moins un service")
    @Valid
    private List<ServiceRendezVousRequest> services;
}
