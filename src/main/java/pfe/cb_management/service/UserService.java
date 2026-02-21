package pfe.cb_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pfe.cb_management.dto.*;
import pfe.cb_management.entity.User;
import pfe.cb_management.enums.Role;
import pfe.cb_management.repository.UserRepository;
import pfe.cb_management.security.JwtService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // ── Lister tous les utilisateurs (hors ADMIN) ────────────
    public List<UserDto> getAllStaff() {
        return userRepository.findByRoleNot(Role.ADMIN)
                .stream().map(this::toDto).toList();
    }

    // ── Lister par rôle ──────────────────────────────────────
    public List<UserDto> getByRole(Role role) {
        return userRepository.findByRole(role)
                .stream().map(this::toDto).toList();
    }

    // ── Récupérer un utilisateur ─────────────────────────────
    public UserDto getById(Long id) {
        return toDto(findUser(id));
    }

    // ── Modifier un utilisateur (admin) ──────────────────────
    public UserDto updateUser(Long id, UpdateUserRequest request) {
        User user = findUser(id);

        if (request.getNom() != null) user.setNom(request.getNom());
        if (request.getPrenom() != null) user.setPrenom(request.getPrenom());
        if (request.getTelephone() != null) user.setTelephone(request.getTelephone());
        if (request.getRole() != null) user.setRole(request.getRole());
        if (request.getSpecialite() != null) user.setSpecialite(request.getSpecialite());
        if (request.getNombresExperiences() != null) user.setNombresExperiences(request.getNombresExperiences());

        // Changer l'email seulement si non pris
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Cet email est déjà utilisé.");
            }
            user.setEmail(request.getEmail());
        }

        // Changer le mot de passe si fourni
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return toDto(userRepository.save(user));
    }

    // ── Supprimer un utilisateur ─────────────────────────────
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur non trouvé.");
        }
        userRepository.deleteById(id);
    }

    // ── Modifier son propre profil (employé/réceptionniste) ──
    public UserDto updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé."));

        if (request.getNom() != null && !request.getNom().isBlank())
            user.setNom(request.getNom());

        if (request.getPrenom() != null && !request.getPrenom().isBlank())
            user.setPrenom(request.getPrenom());

        // Changer l'email seulement si différent et non pris
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Cet email est déjà utilisé.");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getTelephone() != null) user.setTelephone(request.getTelephone());
        if (request.getSpecialite() != null) user.setSpecialite(request.getSpecialite());
        if (request.getNombresExperiences() != null) user.setNombresExperiences(request.getNombresExperiences());

        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new RuntimeException("Mot de passe actuel incorrect.");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        return toDto(userRepository.save(user));
    }

    // ── Activer / Désactiver ─────────────────────────────────
    public UserDto toggleActivation(Long id) {
        User user = findUser(id);
        user.setActivated(!user.isActivated());
        return toDto(userRepository.save(user));
    }

    // ── Helpers ──────────────────────────────────────────────
    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id : " + id));
    }

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .telephone(user.getTelephone())
                .role(user.getRole())
                .activated(user.isActivated())
                .specialite(user.getSpecialite())
                .nombresExperiences(user.getNombresExperiences())
                .build();
    }
}