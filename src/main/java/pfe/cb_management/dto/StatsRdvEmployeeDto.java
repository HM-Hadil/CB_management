package pfe.cb_management.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StatsRdvEmployeeDto {
    private Long   employeeId;
    private String employeeNom;
    private String employeePrenom;
    private int    mois;
    private long   count;
}
