package pfe.cb_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "avis_clientes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AvisCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Référence au rendez-vous (TERMINÉ obligatoire) ─────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rendez_vous_id", nullable = false)
    private RendezVous rendezVous;

    // ── Infos client dénormalisées pour affichage rapide ───────
    @Column(nullable = false)
    private String nomClient;

    @Column(nullable = false)
    private String prenomClient;

    private String telephoneClient;

    // ── Avis ──────────────────────────────────────────────────
    /** Note de 1 à 5 étoiles */
    @Column(nullable = false)
    private Integer note;

    /** Commentaire optionnel */
    @Column(columnDefinition = "TEXT")
    private String commentaire;

    // ── Horodatage ────────────────────────────────────────────
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
