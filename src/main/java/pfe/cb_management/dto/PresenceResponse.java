package pfe.cb_management.dto;

import lombok.*;
import pfe.cb_management.enums.StatutPresence;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PresenceResponse {
    private Long id;
    private Long employeeId;
    private String employeeNom;
    private String employeePrenom;
    private String employeeSpecialite;
    private String date;
    private String heureArrivee;
    private String heureDepart;
    private StatutPresence statut;
    private Double heuresTravaillees;
}
