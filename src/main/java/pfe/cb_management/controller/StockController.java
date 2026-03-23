package pfe.cb_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pfe.cb_management.dto.ProduitStockDto;
import pfe.cb_management.dto.ProduitStockRequest;
import pfe.cb_management.service.StockService;

import java.util.List;

@RestController
@RequestMapping("/api/receptionist/stock")
@PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
@RequiredArgsConstructor
@Tag(name = "Stock", description = "Gestion du stock des produits du salon")
@SecurityRequirement(name = "bearerAuth")
public class StockController {

    private final StockService stockService;

    @GetMapping
    @Operation(summary = "Lister tous les produits en stock")
    public ResponseEntity<List<ProduitStockDto>> getAllProduits() {
        return ResponseEntity.ok(stockService.getAllProduits());
    }

    @GetMapping("/alertes")
    @Operation(summary = "Lister les produits en alerte de stock (quantité ≤ quantité minimum)")
    public ResponseEntity<List<ProduitStockDto>> getProduitsEnAlerte() {
        return ResponseEntity.ok(stockService.getProduitsEnAlerte());
    }

    @PostMapping
    @Operation(summary = "Ajouter un nouveau produit au stock")
    public ResponseEntity<ProduitStockDto> creer(@RequestBody ProduitStockRequest request) {
        return ResponseEntity.ok(stockService.creer(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un produit existant")
    public ResponseEntity<ProduitStockDto> modifier(@PathVariable Long id,
                                                    @RequestBody ProduitStockRequest request) {
        return ResponseEntity.ok(stockService.modifier(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un produit du stock")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        stockService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
