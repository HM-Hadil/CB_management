package pfe.cb_management.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StatsPresenceEmployeeDto {
    private Long   employeeId;
    private String employeeNom;
    private String employeePrenom;
    private long   joursPresent;
}
