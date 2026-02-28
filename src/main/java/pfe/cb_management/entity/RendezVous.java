package pfe.cb_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import pfe.cb_management.enums.StatutRendezVous;
import pfe.cb_management.enums.TypeClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rendez_vous")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Informations client (sans compte obligatoire) ─────
    @Column(nullable = false)
    private String nomClient;

    @Column(nullable = false)
    private String prenomClient;

    private String telephoneClient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeClient typeClient;

    // ── Planification ─────────────────────────────────────
    @Column(nullable = false)
    private LocalDateTime dateDebut;

    @Column(nullable = false)
    private LocalDateTime dateFin;

    @Column(nullable = false)
    private Integer nbHeures;

    // ── Statut ────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutRendezVous statut = StatutRendezVous.EN_ATTENTE;

    // ── Réceptionniste qui a créé le rendez-vous ──────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    // ── Services du rendez-vous ───────────────────────────
    @OneToMany(mappedBy = "rendezVous", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ServiceRendezVous> services = new ArrayList<>();

    // ── Horodatage ────────────────────────────────────────
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
