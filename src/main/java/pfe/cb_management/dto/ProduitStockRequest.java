package pfe.cb_management.dto;

import lombok.*;
import pfe.cb_management.enums.CategorieStock;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProduitStockRequest {

    private String nom;
    private CategorieStock categorie;
    private int quantite;
    private int quantiteMinimum;
    private String unite;
    private BigDecimal prixUnitaire;
}
