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
import pfe.cb_management.service.UserService;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Administration", description = "Gestion des utilisateurs par l'admin")
public class AdminController {

    private final UserService userService;
    private final AuthService authService;

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
}