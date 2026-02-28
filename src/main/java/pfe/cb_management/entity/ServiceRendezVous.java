package pfe.cb_management.entity;

import jakarta.persistence.*;
import lombok.*;
import pfe.cb_management.enums.TypeService;

@Entity
@Table(name = "service_rendez_vous")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ServiceRendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rendez_vous_id", nullable = false)
    private RendezVous rendezVous;

    // Nullable pour les clients MARIAGE (employé non assigné à la création)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private User employee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeService typeService;
}
