package pfe.cb_management.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import pfe.cb_management.enums.Role;
import pfe.cb_management.enums.Specialite;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegisterRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    private String telephone;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;

    @NotNull(message = "Le rôle est obligatoire")
    private Role role;

    // Spécifiques employé (optionnels)
    private Specialite specialite;
    private Integer nombresExperiences;
}
