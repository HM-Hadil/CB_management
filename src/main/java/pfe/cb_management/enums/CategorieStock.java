package pfe.cb_management.enums;

public enum CategorieStock {
    SOINS("Soins"),
    COIFFEUSE("Coiffeure"),
    ESTHETICIENNE("Esthétique"),
    ONGLERIE("Onglerie"),
    MAQUILLEUSE("Maquillage");

    private final String label;

    CategorieStock(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
