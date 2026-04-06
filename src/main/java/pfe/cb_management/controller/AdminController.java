package pfe.cb_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pfe.cb_management.dto.*;
import pfe.cb_management.enums.Role;
import pfe.cb_management.service.AuthService;
import pfe.cb_management.service.StatsService;
import pfe.cb_management.service.UserService;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Administration", description = "Gestion des utilisateurs par l'admin")
public class AdminController {

    private final UserService userService;
    private final AuthService authService;
    private final StatsService statsService;

    @PostMapping("/users")
    @Operation(summary = "Créer un compte employé ou réceptionniste")
    public ResponseEntity<AuthResponse> createUser(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @GetMapping("/users")
    @Operation(summary = "Lister tout le staff")
    public ResponseEntity<List<UserDto>> getAllStaff() {
        return ResponseEntity.ok(userService.getAllStaff());
    }

    @GetMapping("/users/role/{role}")
    @Operation(summary = "Lister par rôle")
    public ResponseEntity<List<UserDto>> getByRole(@PathVariable Role role) {
        return ResponseEntity.ok(userService.getByRole(role));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Récupérer un utilisateur")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Modifier un utilisateur")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Supprimer un utilisateur")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{id}/toggle-activation")
    @Operation(summary = "Activer/Désactiver un compte")
    public ResponseEntity<UserDto> toggleActivation(@PathVariable Long id) {
        return ResponseEntity.ok(userService.toggleActivation(id));
    }

    // ── Statistiques ─────────────────────────────────────────────────────────

    @GetMapping("/stats/rdv")
    @Operation(summary = "RDV par employé par mois pour une année")
    public ResponseEntity<List<StatsRdvEmployeeDto>> statsRdv(
            @RequestParam(required = false) Integer annee) {
        int a = (annee != null) ? annee : LocalDate.now().getYear();
        return ResponseEntity.ok(statsService.getRdvParEmployeeParMois(a));
    }

    @GetMapping("/stats/presence")
    @Operation(summary = "Présence par employé pour un mois/année")
    public ResponseEntity<List<StatsPresenceEmployeeDto>> statsPresence(
            @RequestParam(required = false) Integer mois,
            @RequestParam(required = false) Integer annee) {
        int m = (mois  != null) ? mois  : LocalDate.now().getMonthValue();
        int a = (annee != null) ? annee : LocalDate.now().getYear();
        return ResponseEntity.ok(statsService.getPresenceParEmployeeParMois(m, a));
    }
}