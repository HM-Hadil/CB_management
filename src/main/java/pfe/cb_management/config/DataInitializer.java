
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
        String adminPassword = "Admin@2025"; // mot de passe par défaut sécurisé

        var existingAdmin = userRepository.findByEmail(adminEmail);
        if (existingAdmin.isPresent()) {
            User admin = existingAdmin.get();
            if (!passwordEncoder.matches(adminPassword, admin.getPassword())) {
                admin.setPassword(passwordEncoder.encode(adminPassword));
                userRepository.save(admin);
                log.warn("⚠️ Compte admin existant, mot de passe forcé à la valeur par défaut. Pensez à le changer immédiatement.");
            } else {
                log.info("✅ Compte admin déjà existant et password est à jour.");
            }
            return;
        }

        User admin = User.builder()
                .nom("Administrateur")
                .prenom("Al-Ghanja")
                .email(adminEmail)
                .telephone("+216 75 000 000")
                .password(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .activated(true)
                .build();

        userRepository.save(admin);
        log.info("🌸 Compte admin créé avec succès !");
        log.info("   📧 Email    : {}", adminEmail);
        log.info("   🔑 Password : {}", adminPassword);
        log.info("   ⚠️  Pensez à changer le mot de passe en production !");
    }
}