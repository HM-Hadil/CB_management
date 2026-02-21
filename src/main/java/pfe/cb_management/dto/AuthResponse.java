package pfe.cb_management.dto;

import lombok.*;
import pfe.cb_management.enums.Role;

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
    private boolean activated;          // ← ajouter
    private String specialite;          // ← ajouter
    private Integer nombresExperiences; // ← ajouter
    private String message;
}