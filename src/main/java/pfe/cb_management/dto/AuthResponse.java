package pfe.cb_management.dto;

import lombok.*;
import pfe.cb_management.enums.Role;
import pfe.cb_management.enums.Specialite;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String email;
    private String nom;
    private String prenom;
    private Role role;
    private boolean activated;
    private Set<Specialite> specialites;
    private Integer nombresExperiences;
    private String message;
}
