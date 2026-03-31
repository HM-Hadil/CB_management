package pfe.cb_management.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AvisClienteRequest {

    /** Note obligatoire : 1 à 5 */
    private Integer note;

    /** Commentaire optionnel */
    private String commentaire;
}
