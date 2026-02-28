package pfe.cb_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pfe.cb_management.dto.*;
import pfe.cb_management.enums.Specialite;
import pfe.cb_management.enums.StatutRendezVous;
import pfe.cb_management.enums.TypeService;
import pfe.cb_management.service.RendezVousService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/receptionist")
@PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
@RequiredArgsConstructor
@Tag(name = "Rendez-vous", description = "Gestion des rendez-vous par la réceptionniste")
@SecurityRequirement(name = "bearerAuth")
public class ReceptionnisteController {

    private final RendezVousService rendezVousService;

    // ── CRÉER un rendez-vous ──────────────────────────────────
    @PostMapping("/rendez-vous")
    @Operation(summary = "Créer un nouveau rendez-vous")
    public ResponseEntity<RendezVousResponse> creer(
            @Valid @RequestBody RendezVousRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        RendezVousResponse response = rendezVousService.creer(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── LISTER tous les rendez-vous ───────────────────────────
    @GetMapping("/rendez-vous")
    @Operation(summary = "Lister tous les rendez-vous (du plus récent au plus ancien)")
    public ResponseEntity<List<RendezVousResponse>> listerTous() {
        return ResponseEntity.ok(rendezVousService.listerTous());
    }

    // ── OBTENIR un rendez-vous ────────────────────────────────
    @GetMapping("/rendez-vous/{id}")
    @Operation(summary = "Obtenir un rendez-vous par ID")
    public ResponseEntity<RendezVousResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(rendezVousService.getById(id));
    }

    // ── MODIFIER un rendez-vous ───────────────────────────────
    @PutMapping("/rendez-vous/{id}")
    @Operation(summary = "Modifier un rendez-vous (impossible si TERMINE ou ANNULE)")
    public ResponseEntity<RendezVousResponse> modifier(
            @PathVariable Long id,
            @Valid @RequestBody RendezVousRequest request) {

        return ResponseEntity.ok(rendezVousService.modifier(id, request));
    }

    // ── CHANGER LE STATUT ─────────────────────────────────────
    @PatchMapping("/rendez-vous/{id}/statut")
    @Operation(summary = "Changer le statut : EN_ATTENTE | CONFIRME | ANNULE | TERMINE")
    public ResponseEntity<RendezVousResponse> changerStatut(
            @PathVariable Long id,
            @Valid @RequestBody StatutUpdateRequest request) {

        return ResponseEntity.ok(rendezVousService.changerStatut(id, request.getStatut()));
    }

    // ── SUPPRIMER un rendez-vous ──────────────────────────────
    @DeleteMapping("/rendez-vous/{id}")
    @Operation(summary = "Supprimer un rendez-vous")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        rendezVousService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    // ── EMPLOYÉS DISPONIBLES ──────────────────────────────────
    @GetMapping("/employees/disponibles")
    @Operation(summary = "Lister les employés disponibles pour une spécialité et un créneau",
               description = "Retourne les employés avec la spécialité donnée qui n'ont aucun rendez-vous " +
                             "actif chevauchant la plage [dateDebut, dateDebut + nbHeures]")
    public ResponseEntity<List<UserDto>> getEmployesDisponibles(
            @RequestParam Specialite specialite,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam Integer nbHeures) {

        return ResponseEntity.ok(
                rendezVousService.getEmployesDisponibles(specialite, dateDebut, nbHeures));
    }

    // ── EMPLOYÉS PAR SPÉCIALITÉ ───────────────────────────────
    @GetMapping("/employees/specialite/{specialite}")
    @Operation(summary = "Lister tous les employés actifs d'une spécialité")
    public ResponseEntity<List<UserDto>> getEmployesParSpecialite(
            @PathVariable Specialite specialite) {

        return ResponseEntity.ok(rendezVousService.getEmployesParSpecialite(specialite));
    }

    // ── EMPLOYÉS DISPONIBLES par TypeService (spécialité dérivée auto) ──────────
    @GetMapping("/employees/disponibles/par-service")
    @Operation(
        summary = "Employés disponibles selon le service sélectionné",
        description = "La spécialité est déduite automatiquement du TypeService choisi. " +
                      "Ex: COUPE → COIFFEUSE, SOIN_VISAGE → SOINS. " +
                      "Retourne les employés libres sur [dateDebut, dateDebut + nbHeures]."
    )
    public ResponseEntity<List<UserDto>> getEmployesDisponiblesParService(
            @RequestParam TypeService typeService,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam Integer nbHeures) {

        return ResponseEntity.ok(
                rendezVousService.getEmployesDisponiblesParTypeService(typeService, dateDebut, nbHeures));
    }

    // ── ENUM HELPERS (utiles pour le front) ───────────────────
    @GetMapping("/enums/specialites")
    @Operation(summary = "Lister toutes les spécialités disponibles")
    public ResponseEntity<Specialite[]> getSpecialites() {
        return ResponseEntity.ok(Specialite.values());
    }

    @GetMapping("/enums/statuts")
    @Operation(summary = "Lister tous les statuts de rendez-vous")
    public ResponseEntity<StatutRendezVous[]> getStatuts() {
        return ResponseEntity.ok(StatutRendezVous.values());
    }

    // ── SERVICES GROUPÉS PAR SPÉCIALITÉ ──────────────────────
    @GetMapping("/enums/services")
    @Operation(
        summary = "Tous les services groupés par spécialité",
        description = "Utile pour alimenter les dropdowns : SOINS → [SOIN_VISAGE, SOIN_PIED, ...], COIFFEUSE → [COUPE, BRUSHING, ...]"
    )
    public ResponseEntity<List<TypeServiceGroupeDto>> getServicesGroupes() {
        return ResponseEntity.ok(rendezVousService.getServicesGroupesParSpecialite());
    }
}
