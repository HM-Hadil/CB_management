package pfe.cb_management.dto;

import jakarta.validation.constraints.Email;
import lombok.*;
import pfe.cb_management.enums.Role;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateUserRequest {
    private String nom;
    private String prenom;
    @Email
    private String email;
    private String telephone;
    private String password;       // nullable → ne change que si fourni
    private Role role;
    private String specialite;
    private Integer nombresExperiences;
}