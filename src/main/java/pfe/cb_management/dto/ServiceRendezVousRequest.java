package pfe.cb_management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import pfe.cb_management.enums.TypeService;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ServiceRendezVousRequest {

    // Obligatoire pour client NORMAL, optionnel pour client MARIAGE
    private Long employeeId;

    @NotNull(message = "Le type de service est obligatoire")
    private TypeService typeService;
}
