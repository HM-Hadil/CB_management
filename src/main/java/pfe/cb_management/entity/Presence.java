package pfe.cb_management.entity;

import jakarta.persistence.*;
import lombok.*;
import pfe.cb_management.enums.StatutPresence;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "presence", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employee_id", "date"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Presence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    @Column(nullable = false)
    private LocalDate date;

    private LocalTime heureArrivee;

    private LocalTime heureDepart;

    private LocalTime heureTerminaison;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutPresence statut = StatutPresence.ABSENT;

}
