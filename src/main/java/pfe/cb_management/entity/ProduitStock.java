package pfe.cb_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import pfe.cb_management.enums.CategorieStock;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "produit_stock")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProduitStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategorieStock categorie;

    @Column(nullable = false)
    private int quantite;

    @Column(nullable = false)
    private int quantiteMinimum;

    @Column(nullable = false)
    private String unite;

    @Column
    private String nomFournisseur;

    @Column
    private String reference;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
