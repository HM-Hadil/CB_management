package pfe.cb_management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import pfe.cb_management.enums.StatutService;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ServiceStatutUpdateRequest {

    @NotNull(message = "Le statut est obligatoire")
    private StatutService statut;
}