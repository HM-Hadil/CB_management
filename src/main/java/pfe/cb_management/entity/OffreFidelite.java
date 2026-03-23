package pfe.cb_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "offre_fidelite", uniqueConstraints = {
        @UniqueConstraint(columnNames = "telephone_client")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OffreFidelite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomClient;

    @Column(nullable = false)
    private String prenomClient;

    @Column(name = "telephone_client", nullable = false, unique = true)
    private String telephoneClient;

    @Builder.Default
    @Column(nullable = false)
    private int offresUtilisees = 0;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
