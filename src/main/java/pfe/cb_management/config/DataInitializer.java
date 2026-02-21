
package pfe.cb_management.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pfe.cb_management.entity.User;
import pfe.cb_management.enums.Role;
import pfe.cb_management.repository.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        createAdminIfNotExists();
    }

    private void createAdminIfNotExists() {
        String adminEmail = "admin@alghanja.tn";

        if (userRepository.existsByEmail(adminEmail)) {
            log.info("✅ Compte admin déjà existant — aucune action.");
            return;
        }

        User admin = User.builder()
                .nom("Administrateur")
                .prenom("Al-Ghanja")
                .email(adminEmail)
                .telephone("+216 75 000 000")
                .password(passwordEncoder.encode("Admin@2025"))
                .role(Role.ADMIN)
                .activated(true)
                .build();

        userRepository.save(admin);
        log.info("🌸 Compte admin créé avec succès !");
        log.info("   📧 Email    : {}", adminEmail);
        log.info("   🔑 Password : Admin@2025");
        log.info("   ⚠️  Pensez à changer le mot de passe en production !");
    }
}