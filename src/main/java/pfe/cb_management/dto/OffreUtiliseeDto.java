package pfe.cb_management.dto;

import lombok.*;
import pfe.cb_management.enums.TypeOffre;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OffreUtiliseeDto {
    private TypeOffre typeOffre;
    private LocalDateTime dateUtilisation;
}
