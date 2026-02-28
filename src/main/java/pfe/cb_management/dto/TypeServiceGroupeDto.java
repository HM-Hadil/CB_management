package pfe.cb_management.dto;

import lombok.*;
import pfe.cb_management.enums.Specialite;
import pfe.cb_management.enums.TypeService;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TypeServiceGroupeDto {
    private Specialite specialite;
    private List<TypeService> services;
}
