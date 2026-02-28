package pfe.cb_management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import pfe.cb_management.enums.StatutRendezVous;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StatutUpdateRequest {

    @NotNull(message = "Le statut est obligatoire")
    private StatutRendezVous statut;
}
