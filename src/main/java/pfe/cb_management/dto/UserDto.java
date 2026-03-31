package pfe.cb_management.dto;

import lombok.*;
import pfe.cb_management.enums.Role;
import pfe.cb_management.enums.Specialite;

import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserDto {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private Role role;
    private boolean activated;
    private Set<Specialite> specialites;
    private Integer nombresExperiences;
}
