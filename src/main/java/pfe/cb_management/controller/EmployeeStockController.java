package pfe.cb_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pfe.cb_management.dto.ProduitStockDto;
import pfe.cb_management.dto.UtilisationProduitRequest;
import pfe.cb_management.entity.User;
import pfe.cb_management.service.StockService;

import java.util.List;

@RestController
@RequestMapping("/api/employee/stock")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
@RequiredArgsConstructor
@Tag(name = "Stock (Employée)", description = "Consultation et signalement du stock par les employées")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeStockController {

    private final StockService stockService;

    @GetMapping
    @Operation(summary = "Lister les produits correspondant aux spécialités de l'employée")
    public ResponseEntity<List<ProduitStockDto>> getAllProduits(
            @AuthenticationPrincipal User user) {
        if (user.getSpecialites() == null || user.getSpecialites().isEmpty()) {
            return ResponseEntity.ok(stockService.getAllProduits());
        }
        return ResponseEntity.ok(stockService.getProduitsBySpecialites(user.getSpecialites()));
    }

    @PostMapping("/utiliser")
    @Operation(summary = "Signaler l'utilisation de plusieurs produits en une seule opération")
    public ResponseEntity<List<ProduitStockDto>> utiliserProduits(
            @RequestBody List<UtilisationProduitRequest> utilisations) {
        return ResponseEntity.ok(stockService.utiliserProduits(utilisations));
    }
}
