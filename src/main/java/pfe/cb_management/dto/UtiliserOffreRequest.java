package pfe.cb_management.dto;

import lombok.Getter;
import lombok.Setter;
import pfe.cb_management.enums.TypeOffre;

@Getter @Setter
public class UtiliserOffreRequest {
    private TypeOffre typeOffre;
}
