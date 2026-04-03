package pfe.cb_management.dto;

import lombok.*;
import pfe.cb_management.enums.CategorieStock;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProduitStockDto {

    private Long id;
    private String nom;
    private CategorieStock categorie;
    private int quantite;
    private int quantiteMinimum;
    private String unite;
    private String nomFournisseur;
    private String reference;

    /** true si quantite <= quantiteMinimum */
    private boolean enAlerte;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
