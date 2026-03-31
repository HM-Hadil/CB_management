package pfe.cb_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pfe.cb_management.dto.AvisClienteDto;
import pfe.cb_management.dto.AvisClienteRequest;
import pfe.cb_management.service.AvisClienteService;

import java.util.List;

@RestController
@RequestMapping("/api/receptionist/avis")
@PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
@RequiredArgsConstructor
@Tag(name = "Avis Clientes", description = "Gestion des avis / retours clientes pour les rendez-vous terminés")
@SecurityRequirement(name = "bearerAuth")
public class AvisClienteController {

    private final AvisClienteService avisService;

    @GetMapping
    @Operation(summary = "Lister tous les avis")
    public ResponseEntity<List<AvisClienteDto>> listerTous() {
        return ResponseEntity.ok(avisService.listerTous());
    }

    @PostMapping("/{rendezVousId}")
    @Operation(summary = "Ajouter un avis pour un rendez-vous terminé")
    public ResponseEntity<AvisClienteDto> creer(
            @PathVariable Long rendezVousId,
            @RequestBody AvisClienteRequest request) {
        return ResponseEntity.ok(avisService.creer(rendezVousId, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un avis")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        avisService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
