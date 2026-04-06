package pfe.cb_management.enums;

public enum Specialite {
    SOINS("Soins"),
    COIFFEUSE("Coiffeure"),
    ESTHETICIENNE("Esthétique"),
    ONGLERIE("Onglerie"),
    MAQUILLEUSE("Maquillage");

    private final String label;

    Specialite(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
