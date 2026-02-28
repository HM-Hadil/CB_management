package pfe.cb_management.dto;

import jakarta.validation.constraints.Size;
import lombok.*;
import pfe.cb_management.enums.Specialite;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateProfileRequest {

    private String telephone;
    private Specialite specialite;
    private Integer nombresExperiences;
    private String nom;
    private String prenom;
    private String email;
    // Pour changer de mot de passe
    private String currentPassword;

    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String newPassword;
}
