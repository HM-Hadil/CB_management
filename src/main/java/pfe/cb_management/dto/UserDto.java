package pfe.cb_management.dto;

import lombok.*;
import pfe.cb_management.enums.Role;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserDto {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private Role role;
    private boolean activated;
    private String specialite;
    private Integer nombresExperiences;
}