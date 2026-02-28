package pfe.cb_management.enums;

public enum TypeService {

    // ── Soins ──────────────────────────────────────────────
    SOIN_VISAGE(Specialite.SOINS),
    SOIN_PIED(Specialite.SOINS),
    SOIN_MAIN(Specialite.SOINS),
    HYDRAFACIALE(Specialite.SOINS),

    // ── Coiffeuse ──────────────────────────────────────────
    COUPE(Specialite.COIFFEUSE),
    PROTEINE(Specialite.COIFFEUSE),
    COLORATION_SIMPLE(Specialite.COIFFEUSE),
    BALAYAGE(Specialite.COIFFEUSE),
    SOIN_CAPILLAIRE(Specialite.COIFFEUSE),
    BRUSHING(Specialite.COIFFEUSE),

    // ── Esthéticienne ──────────────────────────────────────
    EPILATION_VISAGE(Specialite.ESTHETICIENNE),
    EPILATION_MOUSTACHE(Specialite.ESTHETICIENNE),
    EPILATION_SOURCILS(Specialite.ESTHETICIENNE),

    // ── Onglerie ───────────────────────────────────────────
    VERNIS_PERMANENT_MAIN(Specialite.ONGLERIE),
    VERNIS_PERMANENT_PIED(Specialite.ONGLERIE),
    GEL_SUR_ONGLE_NATURELLE(Specialite.ONGLERIE),
    GEL_CAPSULE(Specialite.ONGLERIE),
    DEPOSE_VERNIS(Specialite.ONGLERIE),

    // ── Maquillage ─────────────────────────────────────────
    MAQUILLAGE(Specialite.MAQUILLEUSE);

    private final Specialite specialite;

    TypeService(Specialite specialite) {
        this.specialite = specialite;
    }

    public Specialite getSpecialite() {
        return specialite;
    }
}
