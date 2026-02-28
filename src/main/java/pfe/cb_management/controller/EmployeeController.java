package pfe.cb_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pfe.cb_management.dto.RendezVousResponse;
import pfe.cb_management.enums.StatutRendezVous;
import pfe.cb_management.service.RendezVousService;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
@RequiredArgsConstructor
@Tag(name = "Espace Employé", description = "Consultation des rendez-vous assignés à l'employé connecté")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {

    private final RendezVousService rendezVousService;

    // ── MES RENDEZ-VOUS (tous) ────────────────────────────────
    @GetMapping("/mes-rendez-vous")
    @Operation(summary = "Lister tous mes rendez-vous",
               description = "Retourne tous les rendez-vous où l'employé connecté est assigné à au moins un service, triés du plus récent au plus ancien.")
    public ResponseEntity<List<RendezVousResponse>> getMesRendezVous(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                rendezVousService.getMesRendezVous(userDetails.getUsername()));
    }

    // ── MES RENDEZ-VOUS PAR STATUT ────────────────────────────
    @GetMapping("/mes-rendez-vous/statut/{statut}")
    @Operation(summary = "Filtrer mes rendez-vous par statut",
               description = "Statuts disponibles : EN_ATTENTE, CONFIRME, ANNULE, TERMINE")
    public ResponseEntity<List<RendezVousResponse>> getMesRendezVousParStatut(
            @PathVariable StatutRendezVous statut,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                rendezVousService.getMesRendezVousParStatut(userDetails.getUsername(), statut));
    }

    // ── DÉTAIL D'UN RENDEZ-VOUS ───────────────────────────────
    @GetMapping("/mes-rendez-vous/{id}")
    @Operation(summary = "Consulter le détail d'un rendez-vous",
               description = "Retourne le détail uniquement si l'employé connecté est bien assigné à ce rendez-vous.")
    public ResponseEntity<RendezVousResponse> getMesRendezVousById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                rendezVousService.getMesRendezVousById(userDetails.getUsername(), id));
    }
}
