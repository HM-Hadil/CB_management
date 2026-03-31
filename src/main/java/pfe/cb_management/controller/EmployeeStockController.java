package pfe.cb_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pfe.cb_management.dto.ProduitStockDto;
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
    @Operation(summary = "Lister tous les produits en stock")
    public ResponseEntity<List<ProduitStockDto>> getAllProduits() {
        return ResponseEntity.ok(stockService.getAllProduits());
    }

    @PatchMapping("/{id}/decrementer")
    @Operation(summary = "Décrémenter la quantité d'un produit de 1")
    public ResponseEntity<ProduitStockDto> decrementer(@PathVariable Long id) {
        return ResponseEntity.ok(stockService.decrementeQuantite(id));
    }
}
