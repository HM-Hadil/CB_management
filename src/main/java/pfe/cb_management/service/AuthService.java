package pfe.cb_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pfe.cb_management.dto.AuthResponse;
import pfe.cb_management.dto.LoginRequest;
import pfe.cb_management.dto.RegisterRequest;
import pfe.cb_management.entity.User;
import pfe.cb_management.enums.Role;
import pfe.cb_management.repository.UserRepository;
import pfe.cb_management.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // ── INSCRIPTION ──────────────────────────────────────────
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé !");
        }

        User user = User.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .telephone(request.getTelephone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .activated(true)
                .specialite(request.getSpecialite())
                .nombresExperiences(request.getNombresExperiences())
                .build();

        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return buildResponse(user, token, "Compte créé avec succès !");
    }

    // ── CONNEXION ────────────────────────────────────────────
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        String token = jwtService.generateToken(user);
        return buildResponse(user, token, "Connexion réussie !");
    }

    // ── HELPER ───────────────────────────────────────────────
    private AuthResponse buildResponse(User user, String token, String message) {
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .role(user.getRole())
                .activated(user.isActivated())
                .specialite(user.getSpecialite())
                .nombresExperiences(user.getNombresExperiences())
                .message(message)
                .build();
    }
}