package pfe.cb_management.dto;

import lombok.*;
import pfe.cb_management.enums.Specialite;
import pfe.cb_management.enums.StatutService;
import pfe.cb_management.enums.TypeService;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ServiceRendezVousDto {
    private Long id;
    private Long employeeId;
    private String employeeNom;
    private String employeePrenom;
    private Specialite employeeSpecialite;
    private TypeService typeService;
    private StatutService statut;
    private LocalDateTime datePrevue;
    private Integer dureeService;
    private String codeRobe;
}
