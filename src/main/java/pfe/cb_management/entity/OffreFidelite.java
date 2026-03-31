package pfe.cb_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import pfe.cb_management.enums.TypeOffre;

import java.time.LocalDateTime;

/**
 * Chaque ligne = 1 offre utilisée (audit log).
 * Le compteur mensuel se calcule via countByTelephoneClientAndMoisAnnee().
 */
@Entity
@Table(name = "offre_fidelite")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OffreFidelite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomClient;

    @Column(nullable = false)
    private String prenomClient;

    @Column(name = "telephone_client", nullable = false)
    private String telephoneClient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeOffre typeOffre;

    /** Format "yyyy-MM", ex : "2026-03" — mois auquel l'offre a été utilisée */
    @Column(nullable = false, length = 7)
    private String moisAnnee;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
