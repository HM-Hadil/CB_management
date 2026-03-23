package pfe.cb_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pfe.cb_management.dto.ClienteFideliteDto;
import pfe.cb_management.service.FideliteService;

import java.util.List;

@RestController
@RequestMapping("/api/receptionist/fidelite")
@PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
@RequiredArgsConstructor
@Tag(name = "Fidélité", description = "Programme de fidélité : 1 service gratuit tous les 5 services payants")
@SecurityRequirement(name = "bearerAuth")
public class FideliteController {

    private final FideliteService fideliteService;

    @GetMapping("/clientes")
    @Operation(summary = "Lister toutes les clientes avec leurs statistiques de fidélité")
    public ResponseEntity<List<ClienteFideliteDto>> getAllClientesFidelite() {
        return ResponseEntity.ok(fideliteService.getAllClientesFidelite());
    }

    @PostMapping("/utiliser/{telephone}")
    @Operation(summary = "Utiliser une offre pour une cliente",
               description = "Décrémente le nombre d'offres disponibles pour la cliente identifiée par son téléphone")
    public ResponseEntity<ClienteFideliteDto> utiliserOffre(@PathVariable String telephone) {
        return ResponseEntity.ok(fideliteService.utiliserOffre(telephone));
    }
}
